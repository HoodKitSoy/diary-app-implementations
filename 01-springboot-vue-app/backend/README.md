# Spring Boot実装の詳細解説

Spring Bootを使った「My Diary App」のバックエンド実装について、初心者でもわかるように詳しく説明します。

## 1. プロジェクト構造の理解

Spring Bootアプリケーションは「層（レイヤー）アーキテクチャ」という設計パターンを使っています。

```
src/main/java/com/example/mydiaryapp/
├── MyDiaryAppApplication.java     # アプリケーションのエントリーポイント
├── entity/                        # データベースのテーブルに対応するクラス
│   ├── User.java                  # ユーザーテーブル
│   ├── Diary.java                 # 日記テーブル
│   ├── Tag.java                   # タグテーブル
│   └── Image.java                 # 画像テーブル
├── dto/                           # データ転送オブジェクト（APIのリクエスト/レスポンス）
│   ├── AuthRequest.java
│   ├── RegisterRequest.java
│   └── DiaryRequest.java
├── repository/                    # データベースアクセス層
│   ├── UserRepository.java
│   ├── DiaryRepository.java
│   └── TagRepository.java
├── service/                       # ビジネスロジック層
│   ├── AuthService.java
│   ├── DiaryService.java
│   └── ImageService.java
├── controller/                    # Web API層（エンドポイント）
│   ├── AuthController.java
│   └── DiaryController.java
└── security/                      # セキュリティ設定
    ├── SecurityConfig.java
    ├── JwtUtil.java
    ├── JwtAuthenticationFilter.java
    ├── CustomUserDetails.java
    └── CustomUserDetailsService.java
```

## 2. 各層の役割と実装

### 2.1 エンティティ層（Entity）

**役割**: データベースのテーブルをJavaクラスで表現する

#### User.java の詳細解説
```java
@Entity  // このクラスがデータベースのテーブルに対応することを示す
@Table(name = "users")  // テーブル名を指定
@Data  // Lombokがgetter/setterを自動生成
@NoArgsConstructor  // 引数なしコンストラクタを自動生成
@AllArgsConstructor  // 全引数コンストラクタを自動生成
public class User {
    @Id  // 主キーであることを示す
    @GeneratedValue(generator = "UUID")  // UUIDを自動生成
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(length = 36)  // カラムの長さを指定
    private String id;

    @Column(length = 50, nullable = false, unique = true)  // 制約を設定
    private String username;
    
    // 以下同様にフィールドを定義...
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    // 1対多の関係を定義。一人のユーザーが複数の日記を持つ
    private List<Diary> diaries;
    
    @PrePersist  // データベースに保存される前に実行
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate  // データベースで更新される前に実行
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
```

**ポイント**:
- `@Entity`: JPAがこのクラスをデータベーステーブルとして認識
- `@Id`: 主キー（一意の識別子）
- `@Column`: カラムの詳細設定（長さ、必須、ユニークなど）
- `@OneToMany`, `@ManyToOne`: テーブル間の関係を定義

### 2.2 リポジトリ層（Repository）

**役割**: データベースへのアクセス方法を定義

#### UserRepository.java の詳細解説
```java
@Repository  // Springがデータアクセス層として認識
public interface UserRepository extends JpaRepository<User, String> {
    // JpaRepositoryを継承することで基本的なCRUD操作が自動提供される
    // save(), findById(), findAll(), delete() など
    
    // カスタムメソッドを定義
    Optional<User> findByEmail(String email);  // メールアドレスで検索
    Optional<User> findByUsername(String username);  // ユーザー名で検索
    boolean existsByEmail(String email);  // メールアドレスの存在確認
    boolean existsByUsername(String username);  // ユーザー名の存在確認
}
```

**ポイント**:
- `JpaRepository<User, String>`: Userエンティティを、String型のIDで管理
- メソッド名規則に従って書くだけで、SQLが自動生成される
- `findBy...`: 検索メソッド
- `existsBy...`: 存在確認メソッド

### 2.3 サービス層（Service）

**役割**: ビジネスロジック（業務処理）を実装

#### AuthService.java の詳細解説
```java
@Service  // Springがサービス層として認識
@RequiredArgsConstructor  // finalフィールドのコンストラクタを自動生成
public class AuthService {
    private final UserRepository userRepository;  // 依存性注入
    private final PasswordEncoder passwordEncoder;  // パスワード暗号化
    private final AuthenticationManager authenticationManager;  // 認証管理
    private final JwtUtil jwtUtil;  // JWT（認証トークン）ユーティリティ

    public Map<String, Object> register(RegisterRequest request) {
        // 1. メールアドレスの重複チェック
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("このメールアドレスは既に使用されています");
        }
        
        // 2. ユーザー名の重複チェック
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("このユーザー名は既に使用されています");
        }

        // 3. 新しいユーザーを作成
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        // パスワードを暗号化して保存（平文では保存しない）
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        
        // 4. データベースに保存
        User savedUser = userRepository.save(user);
        
        // 5. JWTトークンを生成
        String token = jwtUtil.generateToken(savedUser.getEmail());

        // 6. レスポンス用のデータを作成
        Map<String, Object> response = new HashMap<>();
        response.put("userId", savedUser.getId());
        response.put("username", savedUser.getUsername());
        response.put("email", savedUser.getEmail());
        response.put("token", token);
        
        return response;
    }
}
```

**ポイント**:
- `@Service`: Springがこのクラスをサービス層として管理
- `@RequiredArgsConstructor`: 依存性注入を簡潔に書ける
- パスワードは必ず暗号化して保存
- JWTトークンで認証状態を管理

### 2.4 コントローラ層（Controller）

**役割**: WebAPIのエンドポイントを定義し、HTTPリクエストを処理

#### AuthController.java の詳細解説
```java
@RestController  // REST APIのコントローラであることを示す
@RequestMapping("/api/auth")  // このコントローラのベースURLを設定
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")  // CORS設定（フロントエンドからのアクセス許可）
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")  // POST /api/auth/register エンドポイント
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterRequest request) {
        // @Valid: リクエストデータのバリデーションを実行
        // @RequestBody: HTTPリクエストのボディをJavaオブジェクトに変換
        
        try {
            // サービス層のメソッドを呼び出し
            Map<String, Object> response = authService.register(request);
            // 201 Created ステータスでレスポンスを返す
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            // エラーが発生した場合は400 Bad Requestを返す
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
```

**ポイント**:
- `@RestController`: JSON形式でレスポンスを返すコントローラ
- `@PostMapping`, `@GetMapping`: HTTPメソッドとパスを指定
- `@Valid`: リクエストデータの検証を自動実行
- `ResponseEntity`: HTTPステータスコードとレスポンスボディを制御

### 2.5 セキュリティ層（Security）

#### JWT認証の仕組み

```java
@Component
public class JwtUtil {
    @Value("${jwt.secret}")  // application.propertiesから秘密鍵を取得
    private String secret;

    @Value("${jwt.expiration}")  // トークンの有効期限を取得
    private Long expiration;

    public String generateToken(String email) {
        // JWTトークンを生成
        return Jwts.builder()
                .setSubject(email)  // トークンの主体（ユーザーのメールアドレス）
                .setIssuedAt(new Date())  // 発行日時
                .setExpiration(new Date(System.currentTimeMillis() + expiration))  // 有効期限
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)  // デジタル署名
                .compact();
    }
}
```

## 3. アプリケーション設定

### application.properties の詳細解説
```properties
# データベース接続設定
spring.datasource.url=jdbc:mysql://localhost:3306/my_diary_app
spring.datasource.username=root
spring.datasource.password=password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate設定
spring.jpa.hibernate.ddl-auto=update  # テーブル自動作成・更新
spring.jpa.show-sql=true  # 実行されるSQLをコンソールに表示
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# JWT設定
jwt.secret=mySecretKey123456789012345678901234567890  # 署名用の秘密鍵
jwt.expiration=86400000  # 24時間（ミリ秒）

# ファイルアップロード設定
spring.servlet.multipart.max-file-size=10MB  # 最大ファイルサイズ
spring.servlet.multipart.max-request-size=10MB  # 最大リクエストサイズ

# CORS設定
cors.allowed-origins=http://localhost:5173  # フロントエンドのURL

# サーバー設定
server.port=8080  # バックエンドのポート番号
```

## 4. データの流れ

1. **フロントエンドからAPIリクエスト**
   ```
   POST /api/auth/register
   Content-Type: application/json
   {
     "username": "testuser",
     "email": "test@example.com", 
     "password": "password123"
   }
   ```

2. **Controller でリクエストを受信**
   - `@Valid`でバリデーション実行
   - DTOクラスにマッピング

3. **Service でビジネスロジック実行**
   - 重複チェック
   - パスワード暗号化
   - データベース保存
   - JWT生成

4. **Repository でデータベースアクセス**
   - JPAが自動でSQLを生成・実行
   ```sql
   INSERT INTO users (id, username, email, password_hash, created_at, updated_at) 
   VALUES (?, ?, ?, ?, ?, ?)
   ```

5. **レスポンスを返却**
   ```json
   {
     "userId": "uuid-12345",
     "username": "testuser",
     "email": "test@example.com",
     "token": "jwt.token.string"
   }
   ```

## 5. エラーハンドリング

```java
try {
    // 正常処理
    Map<String, Object> response = authService.register(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
} catch (RuntimeException e) {
    // エラー処理
    return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
}
```

## 6. 起動手順

1. **MySQLデータベース準備**
   ```sql
   CREATE DATABASE my_diary_app CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

2. **アプリケーション起動**
   ```bash
   cd backend
   mvn spring-boot:run
   ```

3. **動作確認**
   ```bash
   curl -X POST http://localhost:8080/api/auth/register \
   -H "Content-Type: application/json" \
   -d '{"username":"test","email":"test@example.com","password":"password123"}'
   ```

## 7. 主要な技術概念

- **依存性注入**: Springが必要なオブジェクトを自動で提供
- **アノテーション**: `@`マークで設定を宣言的に記述
- **ORM（JPA/Hibernate）**: JavaオブジェクトとDBテーブルを自動マッピング
- **JWT認証**: ステートレスな認証トークン
- **CORS**: フロントエンドとバックエンドの異なるポート間通信を許可

このように、Spring Bootは多くの機能を自動化し、開発者は主にビジネスロジックに集中できる設計になっています。
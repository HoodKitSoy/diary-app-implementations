## Gitコミットメッセージの規約

プロジェクトの履歴を分かりやすく管理するため、コミットメッセージは以下の規約に従って記述してください。

### 基本原則

  - **3行で記述:** コミットメッセージは必ず**3行**で構成します。
  - **プレフィックス必須:** 1行目の先頭には、変更内容に応じた**プレフィックス**を必ず付与します。
  - **日本語で記述:** メッセージはすべて日本語で記述します。
  - **何を(What)と何故(Why)を明確に:** 1行目で「何を変更したか」、3行目で「なぜ変更したか」を明確に伝えます。

\<br\>

### コミットメッセージの構造

```
<プレフィックス>: <変更内容の要約>

<変更した理由や背景>
```

1.  **1行目: 要約 (Subject)**

      - `<プレフィックス>: <変更内容の要約>` の形式で記述します。
      - 変更内容が一目でわかるように、簡潔に記述してください。
      - 例: `feat: ユーザープロフィール画像のアップロード機能を追加`

2.  **2行目: 空行 (Blank line)**

      - 要約と詳細を区切るため、必ず空行を入れます。

3.  **3行目: 詳細 (Body)**

      - なぜこの変更が必要だったのか、どのような背景があったのかを具体的に記述します。
      - レビューワーが変更の意図を理解しやすくなります。
      - 例: `ユーザーが自身のプロフィールをよりパーソナライズできるようにするため`

\<br\>

### プレフィックス一覧

コミット内容に応じて、以下のプレフィックスを1行目の先頭に付けてください。

| 接頭辞           | 説明                                                                     |
| ---------------- | ------------------------------------------------------------------------ |
| `fix`            | 既存の機能の**問題**を修正する場合                                       |
| `hotfix`         | **緊急**の変更を追加する場合                                             |
| `add` / `feat`   | 新しいファイルや**機能**を追加する場合                                   |
| `update`         | 既存の機能に問題はないが、**修正**を加えたい場合                         |
| `change`         | **仕様変更**により、既存の機能に修正を加えた場合                         |
| `clean`/`refactor` | コードをリファクタリングし、**改善**する場合                               |
| `improve`        | コードの**改善**をする場合                                               |
| `disable`        | 機能を一時的に**無効**にする場合                                         |
| `remove`/`delete` | ファイルや機能を**削除**する場合                                         |
| `rename`         | ファイル名を**変更**する場合                                             |
| `move`           | ファイルを**移動**する場合                                               |
| `upgrade`        | バージョンを**アップグレード**する場合                                   |
| `revert`         | 以前のコミットに**戻す**場合                                             |
| `docs`           | **ドキュメント**を修正する場合                                           |
| `style`          | コーディング**スタイル**の修正をする場合（インデント、typoなど）         |
| `perf`           | コードの**パフォーマンス**を改善する場合                                 |
| `test`           | **テストコード**を修正・追加する場合                                     |
| `chore`          | ビルドツールなど、上記に当てはまらない修正をする場合                     |

\<br\>

### コミットメッセージの具体例

**例1: 新機能の追加**

```
feat: 管理者向けのダッシュボード機能を追加

売上やユーザー動向を可視化し、迅速な意思決定をサポートするため
```

**例2: バグ修正**

```
fix: ログイン時に稀に発生する500エラーを修正

特定の条件下でセッション情報が正しく取得できていなかった問題を解決
```

**例3: リファクタリング**

```
refactor: EmployeeモデルとSkillモデルの関連処理を改善

N+1問題を解消し、データベースへのクエリパフォーマンスを向上させるため
```
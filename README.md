# Elite Prompt - Java + PostgreSQL Edition

**Tagline:** Create Better AI Prompts. Faster. Smarter.
**Team:** OmRaj Chaudhary
**Type:** TYBCA Mini Project

## Architecture

| Layer | Java/PostgreSQL Implementation |
|-----------|-----------------|
| Database connection | `config/DBConnection.java` (JDBC) |
| Database | PostgreSQL 18 tables `users`, `prompts`, `feedback` (`SERIAL` PK, `ILIKE` search) |
| Password security | BCrypt via `org.mindrot:jbcrypt` |
| Login session | `HttpSession` (`AuthUtil.java`) |
| Web pages | 10x `.jsp` + 15x Servlets (`@WebServlet`, Jakarta EE 11) |
| Search | `ILIKE` (case-insensitive Postgres) |
| Server | Apache Tomcat 11 + Maven WAR |

Generator logic: `util/PromptGenerator.java` (server-side) + JS `buildPrompt()` (client-side live preview).

## New Features (v4 - Quick-wins bundle)

| Feature | How to use | Key files |
|---------|-----------|-----------|
| Favorites (star) | Star icon on any My Prompts card; filter with the star checkbox | `ToggleFavoriteServlet.java`, `PromptDAO.toggleFavorite()` |
| Tags | Comma-separated tags on save/edit (e.g. `instagram, fitness`); shown as `#badges`, searchable + tag filter box | `prompts.tags` column |
| Public Gallery + Clone | Globe icon shares a prompt; `/gallery` lists community prompts (no login needed to browse); Clone copies into your library | `GalleryServlet.java`, `ClonePromptServlet.java`, `gallery.jsp` |
| Export JSON | "Export JSON" button on My Prompts downloads your library | `ExportServlet.java` (`/export`) |
| Quality Score | 0-100 heuristic + tips on generator (server badge + Live Score button); mirrored in JS `scoreRequirement()` | `PromptGenerator.score()/tips()` |
| Pagination | 6 per page on My Prompts, 9 on Gallery; filters preserved in page links | `MyPromptsServlet.java`, `PromptDAO.countFiltered()` |

## Tech Stack (latest, Oct 2026)

- Java 26, Jakarta Servlets 6.1, JSP 4.0, JSTL 3.0 (Jakarta EE 11)
- PostgreSQL 18 + JDBC driver 42.7.13
- Apache Tomcat 11.0.x, Maven 3.9+
- Bootstrap 5, same dark AI CSS/JS

> Tomcat 11 requires Java 17+. This project targets Java 26. Tomcat 9 is EOL
> (Mar 2027) and uses the old `javax.*` packages - this project uses `jakarta.*`.

## Version History

| Date | Java | Tomcat | PostgreSQL / JDBC | Notes |
|------|------|--------|-------------------|-------|
| v1 (initial) | 11 | 9 (`javax.*`) | 14+ / JDBC 42.7.3 | First Java port |
| v2 | 21 LTS | 11 (`jakarta.*`) | 18 / JDBC 42.7.13 | Latest-stack upgrade |
| v3 | 26 | 11 (`jakarta.*`) | 18 / JDBC 42.7.13 | Java 26 upgrade |
| v4 | 26 | 11 (`jakarta.*`) | 18 / JDBC 42.7.13 | Quick-wins: favorites, tags, gallery, export, score, pagination |
| v5 | 26 | 11 (`jakarta.*`) | 18 / JDBC 42.7.13 | Real AI: Pollinations free (no key) + OpenCode Zen (own key), settings page |
| v6 (current) | 26 | 11 (`jakarta.*`) | 18 / JDBC 42.7.13 | Admin dashboard: monitor users, react (promote/demote admin), remove user |

## Project Structure

```
ElitePrompt-Java/
├── pom.xml
├── database/database.sql
├── database/database.sql   (single setup file: fresh install + upgrade + sample data)
├── src/main/java/com/eliteprompt/
│   ├── config/DBConnection.java
│   ├── model/User.java, Prompt.java
│   ├── dao/UserDAO.java, PromptDAO.java, FeedbackDAO.java
│   ├── util/PromptGenerator.java, AuthUtil.java
│   ├── ai/AiProvider.java, ZenAiProvider.java, PollinationsAiProvider.java
│   └── servlet/RegisterServlet.java, LoginServlet.java, LogoutServlet.java,
│       DashboardServlet.java, GeneratorServlet.java, SavePromptServlet.java,
│       MyPromptsServlet.java, EditPromptServlet.java, DeletePromptServlet.java,
│       FeedbackServlet.java, GalleryServlet.java, ClonePromptServlet.java,
│       ExportServlet.java, ToggleFavoriteServlet.java, TogglePublicServlet.java,
│       RunAiServlet.java, SettingsServlet.java,
│       AdminServlet.java, AdminActionServlet.java
└── src/main/webapp/
    ├── index.jsp, about.jsp, register.jsp, login.jsp, dashboard.jsp,
    │   prompt_generator.jsp, my_prompts.jsp, edit_prompt.jsp, feedback.jsp,
    │   gallery.jsp, settings.jsp, admin.jsp
    ├── includes/header.jsp, navbar.jsp, footer.jsp
    ├── assets/css/style.css, assets/js/script.js
    └── WEB-INF/web.xml
```

## AI Integration (v5)

Run any generated prompt through real AI, answer shown side-by-side.
Two providers behind one `AiProvider` interface (strategy pattern):

| Provider | Key needed? | Endpoint | Models |
|----------|-------------|----------|--------|
| Pollinations Free (default) | No | `GET text.pollinations.ai/{prompt}?model=openai` | `openai` (verified live) |
| OpenCode Zen | Yes, your own | `POST opencode.ai/zen/v1/chat/completions` | `qwen3.8-max`, `deepseek-v4-flash`, `nemotron-3.5-lightning-free` |

- Zen keys: Settings page (`/settings`) → saved per user in `users.zen_api_key`, only last 4 chars ever displayed, never hardcoded.
- Without a Zen key, Zen runs show a guidance error; Pollinations always works.
- New files: `ai/` package (3 classes), `RunAiServlet.java` (`/run-ai`), `SettingsServlet.java` (`/settings`), `settings.jsp`, provider+model dropdowns on the generator page.
- New dependency: `org.json:json` (parses Zen's OpenAI-format replies).

## Database Setup (PostgreSQL 18)

**One file for everything:** `database/database.sql` works for a fresh
install AND for upgrading an old database — `CREATE TABLE IF NOT EXISTS`
skips what exists, `ALTER TABLE ... IF NOT EXISTS` adds only missing
columns. No `DROP` anywhere, so rows are never deleted. Sample data
(demo/priya users, 4 prompts, 1 feedback) inserts only when missing.
Demo logins: `demo / demo1234` and `priya / demo1234`.

**Existing database (upgrading from v1-v3):** just run the same file —
```
psql -U postgres -d eliteprompt -f database/database.sql
```
(or paste `database/database.sql` into pgAdmin Query Tool).

## Database Setup (PostgreSQL 18)

1. Install PostgreSQL 18, open pgAdmin or psql.
2. Create database:
   ```sql
   CREATE DATABASE eliteprompt;
   ```
3. Open Query Tool on `eliteprompt`, run `database/database.sql`.
   (The script also works on PostgreSQL 15/16/17.)
4. Edit `src/main/java/com/eliteprompt/config/DBConnection.java`:
   ```java
   URL = "jdbc:postgresql://localhost:5432/eliteprompt";
   USER = "postgres";
   PASSWORD = "your_password";
   ```

## How to Run (Tomcat 11)

1. Install JDK 26 (or newer), Maven 3.9+, Tomcat 11.0.x.
2. Build WAR:
   ```
   cd ElitePrompt-Java
   mvn clean package
   ```
   Output: `target/ElitePrompt.war`
3. Copy `ElitePrompt.war` to `C:\apache-tomcat-11\webapps\` (or `tomcat/webapps/`).
4. Start Tomcat (`startup.bat` / `startup.sh`).
5. Open `http://localhost:8080/ElitePrompt/`

## Admin Dashboard (v6)

Log in as the seeded admin (`Admin@elite`, password `Admin@123`) to open **Admin** in the navbar:
- Stats: users + admins count
- **Users table**: name, username, email, prompt count, joined — with actions
- **Admins table** (separate table, marked in UI): name, username, email, joined — with actions
- **React**: user → promote to admin (copies credentials into `admins`); admin → demote back to user
- **Remove**: user → deletes from `users` (prompts cascade-delete); admin → deletes from `admins`
- Safety guard: an admin cannot remove or demote their own account
- Non-admins trying `/admin` are redirected to their dashboard

New files: `model/Admin.java`, `dao/AdminDAO.java`, `AdminServlet.java` (`/admin`), `AdminActionServlet.java` (`/admin-action`), `admin.jsp`.
New table: `admins` (separate from `users`). Login checks `users` first, then `admins`. The upgrade path in `database.sql` migrates old `is_admin` flags into the new table.

## Usage Flow

1. Register → Login (session set)
2. Dashboard → Quick Generate (now shows Favorites count)
3. Generator: title, category, requirement, tone, format → Generate; check Quality Score + tips; **Run with AI** (Pollinations free, or Zen with your key) → real answer side-by-side
4. Save with tags + optional Gallery sharing → My Prompts → Search / Category / Tag / Favorites filter / pagination / star / share / Export JSON
5. Gallery → browse public prompts → Clone into your library
6. Feedback → Logout

## Viva Notes

- DAO classes use `PreparedStatement` - explain SQL injection prevention.
- Ownership check: every prompt query includes `AND user_id = ?`.
- BCrypt: `BCrypt.hashpw()` on register, `BCrypt.checkpw()` on login.
- `PromptGenerator.generate()` uses `HashMap` of 7 role templates.
- Servlets: `doGet` shows page, `doPost` processes form, `RequestDispatcher.forward()` to JSP.

# QA REPORT — Core Java Ticket Management System

**Tester:** Buffy (QA/Test Engineer mode) · **Date:** 2026-09-12
**Environment:** Windows, bash, JDK/JRE 25.0.1 · Build: `javac -Xlint:all` → **0 errors, 0 warnings**
**Scope:** Compile, execute, observe, report. No source code was modified. No fixes applied.

**Method note:** The app uses 4 separate `static Scanner` instances (Application, User, Employee, Manager). A single piped input stream fails — the 2nd Scanner hits EOF because the 1st buffers the whole stream (root cause proven with an isolated probe). Testing was performed with a paced input driver (`/tmp/drive.sh`, ~0.3s/line) over 7 full application runs, including a 298-step lifecycle run.

---

## PART 1 — CONFIRMED BUGS (reproduced by execution)

---

**BUG ID:** C1
**Severity:** CRITICAL
**Title:** Any non-numeric input at any numeric prompt terminates the entire application (`InputMismatchException`)
**Repro steps / Input:**
1. Main menu → enter `abc` → app dies. (`printf 'abc\n' | java Application`)
2. Signup → role prompt → `abc` → app dies (User.java:64)
3. Employee menu → `abc` → app dies (Employee.java:273)
4. Raise Ticket → Ticket ID → `abc` → app dies (Employee.java:44)
5. Support → Work On Ticket → "1.Start Work" prompt → `xyz` → app dies (Employee.workOnTicket)
**Expected:** Invalid input is rejected with a message; app continues.
**Actual:** Uncaught `java.util.InputMismatchException`, JVM exits (exit code 1), all in-memory data lost.
**Exception:** `InputMismatchException at Scanner.nextInt(...) → Application.main:19 / User.signup:64 / Employee.raiseTicket:44 / Employee.showEmployeeMenu:273 / Employee.workOnTicket` (5 distinct sites reproduced; every `nextInt` in the app has the same defect)
**Affected:** all `Scanner.nextInt()` call sites (Application, User, Employee, Manager)
**Reproducible:** YES

---

**BUG ID:** C2
**Severity:** CRITICAL
**Title:** 6th Manager signup (managers[5] full) silently creates an un-loginable phantom account
**Repro:** Signup 6 managers (any departments) → 6th prints `Employee added successfully.` + `Signup successful!` (no warning) → login with that account → `Invalid username or password` (no User was created) — yet an Employee record permanently occupies `employees[]`.
**Expected:** Reject with "manager capacity full" and create nothing.
**Actual:** Phantom Employee record + success message; account can never log in.
**Affected:** `User.signup` (manager branch, no capacity check on `app.managers`)
**Reproducible:** YES

---

**BUG ID:** C3
**Severity:** CRITICAL
**Title:** users[20] full → signup reports success but silently creates no User (silent data loss)
**Repro:** Signup 20 users, then `u15` → output shows only `Signup successful!` → `1 / u15 / p15` → `Invalid username or password`. User object silently dropped because the `users[]` insert loop finds no null slot.
**Expected:** Clear "user database full" rejection.
**Actual:** Success message; account unusable forever.
**Affected:** `User.signup` (final users[] insert loop)
**Reproducible:** YES

---

**BUG ID:** H1
**Severity:** HIGH
**Title:** SUPPORT login with no manager of that department → silent fall-through to main menu
**Repro:** Signup `supmkt` (department MARKETING, no MARKETING manager) → login `supmkt/sup123` → output jumps straight back to `1. Login 2. Signup...` — no dashboard, no error, though login succeeded internally.
**Expected:** Support dashboard, or explicit "no manager found for your department".
**Affected:** `User.login` (SUPPORT branch — no fallback after manager loop)
**Reproducible:** YES

---

**BUG ID:** H2
**Severity:** HIGH
**Title:** MANAGER login can silently fail (main menu, no message) when no matching Manager object exists
**Repro:** After C2 (6th manager has an Employee record but no Manager object) login routes to the MANAGER branch, finds no `managerId == employeeId`, exits silently to main menu.
**Expected:** Error message or dashboard.
**Affected:** `User.login` (MANAGER branch)
**Reproducible:** YES

---

**BUG ID:** H3
**Severity:** HIGH
**Title:** Duplicate Ticket IDs accepted — globally and per-employee — corrupting assignment targeting
**Repro:** EmpFIN-A raises `401/T401` then `401/T401DUP` (both accepted, both shown in "View My Tickets" and manager views). Same ID also accepted across employees (EmpHR-A `101`, EmpHR-B `101`). When a manager assigns "401", `findTicket` returns only the first match; the second 401 is untouchable as a unique ticket.
**Expected:** Reject duplicate ID.
**Affected:** `Employee.raiseTicket` (no uniqueness check), `Manager.findTicket` (first-match)
**Reproducible:** YES

---

**BUG ID:** H4
**Severity:** HIGH
**Title:** RESOLVED ticket can be reassigned; new assignee closes a ticket they never worked; reassignment is not recorded in history
**Repro:** Ticket 701: assigned → SupHR → IN_PROGRESS → RESOLVED (by SupHR). Manager: `5 / 701 / <SupHR2 id>` → `Ticket reassigned successfully.` SupHR2: Work on 701 → `1` → `Status updated to: CLOSED`. History contains no reassignment event and no record that SupHR resolved it.
**Expected:** Block reassignment of RESOLVED tickets; record reassignment in history.
**Affected:** `Manager.reassignTicket` (only checks CLOSED), `Ticket.addHistory` (no event added)
**Reproducible:** YES

---

**BUG ID:** H5
**Severity:** HIGH
**Title:** Invalid priorities accepted verbatim at ticket creation ("URGENT", "Crashed", any string)
**Repro:** Raise ticket with priority `URGENT` → created with `Priority: URGENT`; multiword title "Hard Disk Crashed" produced priority `Crashed`. `Ticket.updatePriority` exists but is never called. Lowercase `critical` also stored as-is (see M1).
**Expected:** Validate against LOW/MEDIUM/HIGH/CRITICAL.
**Affected:** `Employee.raiseTicket`, `Ticket` constructor
**Reproducible:** YES

---

**BUG ID:** H6
**Severity:** HIGH
**Title:** Ticket ID 0, negative IDs, and any integer accepted (e.g. `-5`)
**Repro:** Employee raises ticket with ID `-5` → `Ticket ID: -5 ... Status: OPEN` created and fully functional.
**Expected:** Reject non-positive/out-of-range IDs.
**Affected:** `Employee.raiseTicket`
**Reproducible:** YES

---

**BUG ID:** H7
**Severity:** HIGH
**Title:** Cross-department assignment and reassignment succeed (no department guard)
**Repro:** IT Manager assigns IT ticket to an HR-department support ID present in its lookup path → `Ticket assigned successfully.` Manager reassignment across departments also accepted (only ID lookup limits it). Additionally, `supportWorkLoad` counts only the manager's own `employee[]`, so cross-department assignments are not counted in the assignee's real department workload.
**Expected:** Manager restricted to own department's employees/tickets/support.
**Affected:** `Manager.assignTicket`, `Manager.reassignTicket` (no department comparison)
**Reproducible:** YES

---

**BUG ID:** M1
**Severity:** MEDIUM
**Title:** Manager "High/Critical" filter misses lowercase `critical` (inconsistent comparison)
**Repro:** Tickets `801 (critical)`, `802 (HIGH)`, `803 (LOW)` → Manager menu 2 → only 802 listed. Cause: `priority.equalsIgnoreCase("HIGH") || priority.equals("CRITICAL")` (Manager.java:178).
**Expected:** Case-insensitive for both.
**Reproducible:** YES

---

**BUG ID:** M2
**Severity:** MEDIUM
**Title:** Status filter fails for lowercase input (`open` returns "No open tickets found" for OPEN tickets)
**Repro:** Manager menu 3 → employee with OPEN tickets → status `open` → `No open tickets found.` Reproduced twice in separate runs.
**Affected:** `Manager.showManagerMenu` option 3
**Reproducible:** YES

---

**BUG ID:** M3
**Severity:** MEDIUM
**Title:** Invalid signup role (0, 4, 9, −1) discards all entered data without re-prompt
**Repro:** Signup with role `0` → `invalid input` → back to main menu; username/password/name/email/phone/department discarded; user must restart the form.
**Expected:** Re-ask role or restart cleanly with message.
**Affected:** `User.signup`
**Reproducible:** YES

---

**BUG ID:** M4
**Severity:** MEDIUM
**Title:** Ticket Title/Description accept exactly one token — multiword input corrupts fields
**Repro:** Title `Hard`, Description `Disk`, Priority `Crashed` → all consumed as separate fields (`Priority: Crashed` ticket created). Uses `sc.next()` not line input.
**Affected:** `Employee.raiseTicket`
**Reproducible:** YES

---

**BUG ID:** L1
**Severity:** LOW
**Title:** Work-on-ticket with numeric input ≠ 1 is a silent no-op
**Repro:** Support works on ASSIGNED ticket, answers `2` at "1.Start Work" → returns to menu with no message at all.
**Affected:** `Employee.workOnTicket`
**Reproducible:** YES

---

**BUG ID:** L2
**Severity:** LOW
**Title:** Usernames are case-sensitive for both duplicate check and login
**Repro:** Signup `mgrhr`; login `MGRHR/m1` → `Invalid username or password`. Duplicate check is exact-match, so `MgrHR` would be a distinct account.
**Reproducible:** YES

---

**BUG ID:** L3
**Severity:** LOW
**Title:** Cosmetic/message defects
**Repro:** `ticket alresy assigned to same employee` (typo); support menu prints `2. Work On Ticket \n 3.View My Workload` as garbled lines; inconsistent `Invalid Input.` / `invalid input` strings.
**Reproducible:** YES

---

## PART 2 — INFO / DESIGN OBSERVATIONS (Phase 18, from source)

1. **4 static Scanner instances** (Application, User, Employee, Manager) on one System.in — proven root cause of piped-input EOF failure; hazardous for automation.
2. **Dummy object anti-pattern:** `Application` creates `new User(0,"","")` solely to call `signup`/`login`.
3. **`Application.supportTeam[20]` is dead** — declared, never used (zero references beyond declaration). Real routing uses each Manager's private `supportTeam[5]` → 6th support signup for the same manager prints `Support team is full.` but still says `Signup successful!` (silent partial record; support exists in `employees[]` only and can log in but is invisible to assignment).
4. **Dual bookkeeping:** same business data in `Application.employees[]` + per-Manager `employee[]`/`supportTeam[]`.
5. **Silent capacity failures everywhere:** `Employee list is full.` + `Signup successful!` (employees[20]); `Support team is full.` + success; managers/users silently dropped (C2/C3). No transactional rollback.
6. **Unreachable/dead code:** `Ticket.displayHistory()` (history feature unreachable from any menu), `Ticket.updatePriority()`, `Manager.viewTicket()`, `viewTicketSummary()`, `viewAllEmployees()`, `viewSupportTeam()`, `Employee.viewAssignedTicket()`, commented-out history line in `raiseTicket`, and `main()` methods in Ticket/Manager/User/Employee.
7. **History format weakness:** entries are `STATUS - person` strings; assignment records the manager name, reassignment records nothing (see H4); `history[100]` silently overflows beyond 100 events (drop, no error).
8. **ID semantics:** `Employee.counter` is static and global — employee IDs are creation-order across all roles/departments; Manager.managerId = the manager's Employee ID (coupling).
9. **Case-sensitive department matching** (`manager.department.equals(emp.getDepartment())`) — employee/support with dept `hr` under manager `HR` are never linked (reproduced: mgrx(HR) → `Employee not found` for dept-`hr` employee).
10. **Login-loop defect:** SUPPORT/MANAGER logins print no failure message when they silently do nothing (H1/H2); `loginSuccess` is only set for EMPLOYEE logins.
11. **No persistence:** everything in-memory; JVM restart loses all users/tickets (verified). Per spec this is reported as observed limitation, not assumed defect.
12. **Session flow (Phase 16) is otherwise clean:** logout → next login shows correct dashboard, no identity leakage, no duplicate object creation on repeated login (verified across all runs).

---

## PART 3 — NOT TESTABLE

- **Viewing ticket history** — `displayHistory()` has no menu path (feature unreachable; recording verified only indirectly).
- Empty/blank input at prompts — `Scanner.next()` skips whitespace; blank submission impossible by design.
- True concurrency — single-threaded console; sequential simulation only (as permitted).
- `stale .class` behavior — Phase 1 deleted and rebuilt them; can't test what was removed.

---

## PART 4 — FINAL SUMMARY TABLE

| Metric | Count |
|---|---|
| Total tests/checks executed | **139** |
| Passed | **95** |
| Failed | **36** |
| Blocked / Not testable | **8** |
| CRITICAL bugs | **3** (C1, C2, C3) |
| HIGH bugs | **7** (H1–H7) |
| MEDIUM bugs | **4** (M1–M4) |
| LOW bugs | **3** (L1–L3) |

**Notable passes:** clean compile; correct signup linking in all 3 orders (A/B/C); department isolation of manager views for well-formed data; 5-ticket limit enforced with no partial record; invalid login rejection; CLOSED ticket blocked from assignment; closed-ticket reassignment blocked; invalid numeric menu choices handled gracefully; workload correctly drops CLOSED tickets (1→0 observed); cross-department manager search correctly returns "Employee not found" for valid-but-foreign IDs.

---

## PART 5 — END-TO-END WORKFLOW VERDICT

**SIGNUP → LOGIN → EMPLOYEE RAISES TICKET → MANAGER FINDS EMPLOYEE → MANAGER FINDS SUPPORT → ASSIGN → SUPPORT LOGINS → SUPPORT VIEWS ASSIGNED → SUPPORT WORKS → RESOLVES → CLOSES → HISTORY:**

✅ **The pipeline WORKS end-to-end — but only under strictly numeric, single-token input.** Fully demonstrated (ticket 101): EmpHR-A raised 101 → MgrHR found the employee and support → assigned to SupHR → SupHR logged in, saw the assigned ticket, moved it ASSIGNED→IN_PROGRESS→RESOLVED→CLOSED, workload went 1→0.

It **breaks in four observed places**:
1. **SUPPORT WORKS step is a crash trap:** non-numeric input at the `1.Start Work` prompt (or any numeric prompt) kills the JVM with `InputMismatchException` (C1).
2. **HISTORY step cannot be performed:** no menu option displays ticket history (`displayHistory()` unreachable).
3. Multiword ticket title/description desynchronizes input and corrupts subsequent fields (M4).
4. Duplicate ticket IDs make ASSIGN target the wrong/first ticket (H3); silent array overflow (C2/C3) makes the flow die at LOGIN with a misleading "Invalid username or password".

# Team300 Bolt Development's README
Endpoints implemented <br />

- "/login" GET - Returns the login page.
- "/login" POST – Authenticates the provided username and password of the user. If this is correct, a token is provided as a cookie and the user is sent to their dashboard. <br/>

- "/account" GET – Returns an account page with the user’s information.
- "/editAccount" GET - Populates the edit page with a user's details. 
- "/editAccount" POST – Modifies the user’s information to match those provided.<br/>

- "/dashboard" GET – Returns the dashboard page with the projects the user is enrolled in.
- "/dashboard/newProject" GET – Returns a page with a form used to create a new project.
- "dashboard/saveProject" POST – Saves or creates a new project with the provided details.<br/>

- "dashboard/editProject/{projectId}" GET – Returns a form to modify a projects information.
- "dashboard/deleteProject/{projectId}" POST - Deletes the project with the specified id from the database.<br/>

- "/register" GET – Returns the registration page.
- "/register" POST – Creates a new user with the provided information. <br/>

- "/project/{projectId}" GET – Returns the project page with the projects information.
- "/project/{projectId}/newSprint" GET - Returns a page with a form used to create a new sprint under the specified project.
- "/project/{projectId}/saveSprint" POST - Saves or creates a new sprint with the provided details.
- "/project/{projectId}/editSprint/{sprintId}" GET – Returns a form to modify a sprints information.
- "/project/{projectId}/deleteSprint/{sprintId}" GET - Deletes the sprint with the specified id from the database.
- "/project/{projectId}/getAllSprints" GET - Retrieves all the sprints for a specific project.
- "/project/{projectId}/verifySprint" POST - Verifies the sprint details from user input.
- "/sprint/{sprintId}/editSprint" 
- "/verifyProject/{projectId}" POST - Validates the projects details from user input.
- "/error" GET - Returns the Error page.
- "/users" GET - Get the list of users page.
- "/usersList" GET - Get the current table page of users. 
- "/usersList/removeRole" DELETE - Remove the selected role from a user.
- "/user/{userId}/addRole" POST - Add the selected role to a user.

Basic project template using `gradle`, `Spring Boot`, `Thymeleaf` and `Gitlab CI`.

## Basic Project Structure

- `systemd/` - This folder includes the systemd service files that will be present on the VM, these can be safely ignored.
- `runner/` - These are the bash scripts used by the VM to execute the application.
- `shared/` - Contains (initially) some `.proto` contracts that are used to generate Java classes and stubs that the following modules will import and build on.
- `identityprovider/` - The Identity Provider (IdP) is built with Spring Boot, and uses gRPC to communicate with other modules. The IdP is where we will store user information (such as usernames, passwords, names, ids, etc.).
- `portfolio/` - The Portfolio module is another fully fledged Java application running Spring Boot. It also uses gRPC to communicate with other modules.

## How to run

### 1 - Generating Java dependencies from the `shared` class library
The `shared` class library is a dependency of the two main applications, so before you will be able to build either `portfolio` or `identityprovider`, you must make sure the shared library files are available via the local maven repository.

Assuming we start in the project root, the steps are as follows...

On Linux: 
```
cd shared
./gradlew clean
./gradlew publishToMavenLocal
```

On Windows:
```
cd shared
gradlew clean
gradlew publishToMavenLocal
```

*Note: The `gradle clean` step is usually only necessary if there have been changes since the last publishToMavenLocal.*

### 2 - Identity Provider (IdP) Module
Assuming we are starting in the root directory...

On Linux:
```
cd identityprovider
./gradlew bootRun
```

On Windows:
```
cd identityprovider
gradlew bootRun
```

By default, the IdP will run on local port 9002 (`http://localhost:9002`).

### 3 - Portfolio Module
Now that the IdP is up and running, we will be able to use the Portfolio module (note: it is entirely possible to start it up without the IdP running, you just won't be able to get very far).

From the root directory (and likely in a second terminal tab / window)...
On Linux:
```
cd portfolio
./gradlew bootRun
```

On Windows:
```
cd portfolio
gradlew bootRun
```

By default, the Portfolio will run on local port 9000 (`http://localhost:9000`)

### 4 - Example User Logins
|Username|Password|Roles|
|----|----|----|
|MattyBacon|Pig2Bacon|Course Administrator|
|FurretFive|5Furrets|Teacher|
|HeadTutor|Knees&2s|Student, Teacher, Course Administrator|
|Alex|2000Fish|Student|

### 5 - Example Project details
|Project Name|Description|Start Date|End Date|
|----|----|----|----|
|SENG 303| This is a third year software engineering project|21/07/2022|21/03/2021|
|Electric car project| This is a mechanical third year project|21/07/2022|21/03/2021|
|TDD 2022| This is a project to focus on test driven development|21/07/2022|20/03/2021|
|Retail Management 2022| This is to manage retail jobs|21/07/2022|20/03/2021|
|Project 2022||10/08/2022|10/04/2023|

## Contributors

- SENG302 teaching team
- Saskia van der Peet
- Jack McCorkindale
- Prableen Oberoi
- Sonia Ra
- Yaoce Yang
- James Claridge
- Rory Holmes

## License
Apache 2.0

## Dependencies
### Shared
- `io.grpc:grpc-netty-shaded:1.40.1`
- `io.grpc:grpc-protobuf:1.40.1`
- `io.grpc:grpc-stub:1.40.1`
- `org.jetbrains:annotations:20.1.0`
- `com.h2database:h2:1.4.200`
- `org.springframework.boot:spring-boot-starter-data-jpa`
- `jakarta.annotation:jakarta.annotation-api:1.3.5`

### Portfolio
- `org.springframework.boot:spring-boot-starter-web`
- `org.springframework.boot:spring-boot-starter-thymeleaf`
- `org.springframework.boot:spring-boot-starter-security`
- `org.springframework.boot:spring-boot-starter-data-jpa`
- `com.h2database:h2:1.4.200`
- `nz.ac.canterbury.seng302:shared:1.0`
- `net.devh:grpc-client-spring-boot-starter:2.13.1.RELEASE`
- `io.jsonwebtoken:jjwt-api:0.11.0`
- `group: 'org.springframework.boot', name: 'spring-boot-starter-validation', version: '2.6.4'`
- `group: 'org.mariadb.jdbc', name: 'mariadb-java-client', version: '3.0.4'`
- `org.springframework.boot:spring-boot-devtools`
- `org.springframework.boot:spring-boot-starter-test`
- `io.cucumber:cucumber-java:6.10.4`
- `io.cucumber:cucumber-junit:6.10.4`

### Identity Provider
- `org.springframework:spring-web`
- `org.springframework:spring-web`
- `'org.springframework.boot:spring-boot-starter-web'`
- `'org.springframework.boot:spring-boot-starter'`
- `'org.springframework.boot:spring-boot-starter-data-jpa'`
- `'com.h2database:h2:1.4.200'`
- `nz.ac.canterbury.seng302:shared:1.0`
- `net.devh:grpc-spring-boot-starter:2.13.1.RELEASE`
- `io.jsonwebtoken:jjwt-api:0.11.0`
- `io.jsonwebtoken:jjwt-impl:0.11.0`
- `io.jsonwebtoken:jjwt-jackson:0.11.0`
- `nz.ac.canterbury.seng302:shared:1.0`
- `org.mariadb.jdbc:mariadb-java-client:2.1.2`
- `org.springframework.boot:spring-boot-devtools`
- `org.springframework.boot:spring-boot-starter-test`
- `io.grpc:grpc-testing:1.45.0`
- `io.cucumber:cucumber-java:6.10.4`
- `io.cucumber:cucumber-junit:6.10.4`

## References

- [Spring Boot Docs](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [Spring JPA docs](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Thymeleaf Docs](https://www.thymeleaf.org/documentation.html)
- [Learn resources](https://learn.canterbury.ac.nz/course/view.php?id=13269&section=9)


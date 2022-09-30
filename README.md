# LENSfolio - by Bolt Development

LENSfolio is an application to manage 
LENSfolio manages projects, events, deadlines and documentation in one place. It is a compact web application compatible on all devices, ideal for  students and teachers to manage projects in a learning environment. 

**Key Features:**
- Teachers and Course Administrators have the ability to set up projects, add events, deadlines and milestones.
- Customised groups can be created with members added. 
- Groups can connect their GitLab repository to follow its activity within LENSfolio. 
- Students can add evidence linked to a project. 
- A list of users can be viewed, as well as other users evidence pages.

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

### 6 - Example Groups 
|Group Short Name|Group Long Name|
|----|----|
|Team 400|400 Bad Request|
|Team 300|Bolt Development|
|Team 100| Lens Development|
|Team 200|200 Cats|
|Team 700|Cows Cows Cows|
|Team 800|The Spartans|


Basic project template using `gradle`, `Spring Boot`, `Thymeleaf` and `Gitlab CI`.

## Basic Project Structure

- `systemd/` - This folder includes the systemd service files that will be present on the VM, these can be safely ignored.
- `runner/` - These are the bash scripts used by the VM to execute the application.
- `shared/` - Contains (initially) some `.proto` contracts that are used to generate Java classes and stubs that the following modules will import and build on.
- `identityprovider/` - The Identity Provider (IdP) is built with Spring Boot, and uses gRPC to communicate with other modules. The IdP is where we will store user information (such as usernames, passwords, names, ids, etc.).
- `portfolio/` - The Portfolio module is another fully fledged Java application running Spring Boot. It also uses gRPC to communicate with other modules.


## Testing
The application comes with 2 types of testing. Unit testing using junit and acceptance testing using cypress.

### Unit Testing
Running the unit tests is very similar to running the application itself. Starting from the project root run the following commands for IDP:

On Linux:
```
cd identityprovider
./gradlew test
```

On Windows:
```
cd identityprovider
gradlew test
```

And on the Portfolio module:

On Linux:
```
cd portfolio
./gradlew test
```

On Windows:
```
cd portfolio
gradlew test
```

### Acceptance Testing
We are using Cypress for our acceptance testing. Please find the steps for installing and running this software below. All commands should be run in the parent project directory. These require the application to be running.

```
npm install
```
This will install Cypress and its dependencies

To run the feature files one at a time run:
```
npx cypress open
```
Click E2E Testing

Select your browser
Click on the feature file that you are testing to run all the relevant tests

Alternativly, to run all the tests at once:
```
npx cypress run
```

Note: Between tests you will need to rerun the application to reset the database. You can also run the contents of the cypress-data.sql file on your selected database.

## Endpoints implemented <br />

### Login
- "/login" GET - Returns the login page.
- "/login" POST – Authenticates the provided username and password of the user. If this is correct, a token is provided as a cookie and the user is sent to their dashboard. <br/>

### Register
- "/register" GET – Returns the registration page.
- "/register" POST – Creates a new user with the provided information. <br/>


### Account
- "/account" GET – Returns an account page with the user’s information.
- "/account/roles" GET - Returns a list of roles the user has.
- "/editAccount" GET - Populates the edit page with a user's details. 
- "/editAccount" POST – Modifies the user’s information to match those provided.<br/>


### Dashboard
- "/dashboard" GET – Returns the dashboard page with the projects the user is enrolled in.


### Project
- "/project" GET - Returns the list of projects
- "/project" POST - Saves project object with the provided details
- "/verifyProject/{projectId}" POST - Validates the projects details from user input.
- "/project/{projectId}" GET – Returns the project page with the projects' information.
- "/project/{projectId}" DELETE - Delete the specified project.


### Sprint
- "/project/{projectId}/sprint" POST - Saves or creates a new sprint with the provided details.
- "/project/{projectId}/newSprint" GET - Returns new page for creating a sprint.
- "/sprint/{sprintId}/editSprint" POST - Saves a sprint's start and end date with the provided dates.
- "/project/{projectId}/editSprint/{sprintId}" GET – Returns a form to modify a sprints' information.
- "/project/{projectId}/deleteSprint/{sprintId}" GET - Deletes the sprint with the specified id from the database.
- "/project/{projectId}/getAllSprints" GET - Retrieves all the sprints for a specific project.
- "/project/{projectId}/verifySprint" POST - Verifies the sprint details from user input.
- "/project/{projectId}/sprint/{sprintId}/accordion" - GET - Returns the sprint with the specified project Id.
- "/error" GET - Returns the Error page.

### Users
- "/users" GET - Get the list of users page.
- "/usersList" GET - Get the current table page of users. 
- "/usersList/removeRole" DELETE - Remove the selected role from a user.
- "/user/{userId}/addRole" POST - Add the selected role to a user.
- 

### Groups
- "/groups" GET - Get the groups page.
- "/groups/list" GET - Get the list of groups.
- "/groups/{groupId}" DELETE - Delete the selected group but not the members within the group.
- "/groups/{groupId}" GET - Get the details of the selected group.
- "/groups/unassigned" GET - Get the members and details of the group of users with no assigned group.
- "/groups/teachers" GET - Get the members and details of the group of users with a Teacher role.
- "/groups" POST - Create a new group.
- "/groups/{groupId}/removeMembers" POST - Remove selected members from a group but not as users of LENSFolio.
- "/groups/{groupId}/addMembers" POST - Add selected members to a group.
- "/groups/{groupId}/addMembers" POST - Add selected members to a group.
- "/group/user/{userId}" GET - Gets the fragment for a user row in the group settings page.

### Events
- "/project/{projectId}/events" GET - Get all the events related to the specified project.
- "/project/{projectId}/saveEvent" POST - Save the event's new or updated details.
- "project/{projectId}/event/{eventId}/delete" DELETE - Delete the specified deadline.
- "event/edit" Message Mapping - Websocket End Point for editing Event.

### Deadlines
- "/project/{projectId}/deadlines" GET - Get all the deadlines related to the specified project.
- "/project/{projectId}/saveDeadline" POST - Save the deadline's new or updated details.
- "/{projectId}/deleteDeadline/{deadlineId}" DELETE - Delete te specified deadline.
- "deadline/edit" Message Mapping - Websocket End Point for editing Deadline.

### Milestones
- "/project/{projectId}/milestones" GET - Get all the milestones related to the specified project.
- "/project/{projectId}/milestone" POST - Save the milestone's new or updated details.
- "/project/{projectId}/milestone/{milestoneId}/delete" DELETE - Delete the specified milestone.
- "/milestone/edit" - Message Mapping - Websocket End Point for editing Milestone

### Evidence
- "evidence/{userId}" GET- Get all the evidence for a specific user.
- "/evidence/{userId}/{evidenceId}" GET - Get a user's specific piece of evidence.
- "/evidence/{userId}/getNewEvidence" GET - Gets a user's newly created evidence.
- "/evidence/{userId}/saveEvidence" POST - Save the updated/new details of a piece of evidence.
- "/evidence/{userId}/{evidenceId}/editEvidence" GET - Updates the evidence with the specified details.
- "/evidence/{userId}/{evidenceId}/deleteEvidence" DELETE - Delete the specified evidence.
- "/evidence/{userId}/getEvidenceList" GET - Gets the evidence list of the user with the given user ID.
- "evidence/edit" Message Mapping - Websocket End Point for editing Evidence.

### Repo
- "/repo/{groupId}" GET - Get the repository for the specified group.
- "/repo/{groupId}/settings" GET - Get the setting components of a group's repository.
- "/repo/{groupId}/save" POST - Save the group's repository with the provided information.


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
- `junit:junit:4.13.1`
- `group: 'org.springframework', name: 'spring-websocket', version: '5.3.22'`
- `group: 'org.springframework', name: 'spring-messaging', version: '5.3.22'`

### Identity Provider
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

StompJS License: Apache 2.0 License

## References

- [Spring Boot Docs](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [Spring JPA docs](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Thymeleaf Docs](https://www.thymeleaf.org/documentation.html)
- [Learn resources](https://learn.canterbury.ac.nz/course/view.php?id=13269&section=9)


# Team300 Bolt Development's README
Endpoints implemented <br />

- "/login" GET - Sends the user to the login page. <br />
- "/login" POST - Attemps to connect to server and then authenticate user's username and password. If successful the a token is created for the users's session. If unsuccessful the user is redirected to the login page. <br />
- "/account" GET - Populates the user's page with thier details.
- "/editAccount" GET - Populates the edit page with a user's details. 
- "/editAccount" POST - Attempts to save the edited user's details into the database, if successful it redirects the user to thier account page, otherwise it leaves them on the edit page.
- "/dashboard" GET - Populates the users dashboard with projects they have.
- "/dashboard/newProject" GET - If the user has the correct role to create a project they are directed to the project form, if not they are redirected to the dashboard.
- "dashboard/saveProject" POST - Checks that the user has the correct role to be saving a project, if not they are are redirected to dashboard. If they do, they project is saved then the user is redirected to the dashboard.<br />
- "dashboard/editProject/{projectId}" GET - Opens the edit page and populates it with the given projects details.
- "dashboard/deleteProject/{projectId}" GET - Deletes the project with the specified id from the database.
- "/register" GET - The get message to return an empty registration page.
- "/register" POST - Attemps to register the user with the given details provided in the form on the page, if successful the user is redirected to the login page. If not, then errors are displaye as to why it was unsuccessful. 
- "/project/{projectId}" GET - This call adds project details, sprints and current user roles to the project page.
- "/project/{projectId}/newSprint" GET - This calls displays a page for adding a new sprint.
- "/project/{projectId}/saveSprint" POST - This calls saves a sprint and redirects the user to the project page.
- "/project/{projectId}/editSprint/{sprintId}" GET - This call directs the user to a page specifically for editing a sprint.
- "/project/{projectId}/deleteSprint/{sprintId}" GET - This call deletes a sprint and redirects back to the project page.

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

## Contributors

- SENG302 teaching team
- Saskia van der Peet
- Jack McCorkindale
- Prableen Oberoi
- Sonia Ra
- Yaoce Yang
- James Claridge
- Rory Holmes

## References

- [Spring Boot Docs](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [Spring JPA docs](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Thymeleaf Docs](https://www.thymeleaf.org/documentation.html)
- [Learn resources](https://learn.canterbury.ac.nz/course/view.php?id=13269&section=9)

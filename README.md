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
- "/project/{projectId}/sprint" POST - Saves or creates a new sprint with the provided details.
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

- "/project/{projectId}/deadlines" GET - Get all the deadlines related to the specified project.
- "/project/{projectId}/saveDeadline" POST - Save the deadline's new or updated details.
- "/{projectId}/deleteDeadline/{deadlineId}" DELETE - Delete te specified deadline.

- "/project/{projectId}/events" GET - Get all the events related to the specified project.
- "/project/{projectId}/saveEvent" POST - Save the event's new or updated details.
- "project/{projectId}/event/{eventId}/delete" DELETE - Delete the specified deadline.


- "evidence/{userId}" GET- Get all the evidence for a specific user.
- "/evidence/{evidenceId}" GET - Get a user's specific piece of evidence.
- "/evidence" POST - Save the updated/new details of a piece of evidence.

- "/repo/{groupId}" GET - Get the repository for the specified group.
- "/repo/{groupId}/settings" GET - Get the setting components of a group's repository.
- "/repo/{groupId}/save" POST - Save the group's repository with the provided information.
- 

- "deadline/edit" Message Mapping - Websocket End Point for editing Deadline.
- "event/edit" Message Mapping - Websocket End Point for editing Event.


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

### 6 - Example Groups 
|Group Short Name|Group Long Name|
|----|----|
|Team 400|400 Bad Request|
|Team 300|Bolt Development|
|Team 100| Lens Development|
|Team 200|200 Cats|
|Team 700|Cows Cows Cows|
|Team 800|The Spartans|

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

## StompJS License

                                 Apache License
                           Version 2.0, January 2004
                        http://www.apache.org/licenses/

   TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION

   1. Definitions.

      "License" shall mean the terms and conditions for use, reproduction,
      and distribution as defined by Sections 1 through 9 of this document.

      "Licensor" shall mean the copyright owner or entity authorized by
      the copyright owner that is granting the License.

      "Legal Entity" shall mean the union of the acting entity and all
      other entities that control, are controlled by, or are under common
      control with that entity. For the purposes of this definition,
      "control" means (i) the power, direct or indirect, to cause the
      direction or management of such entity, whether by contract or
      otherwise, or (ii) ownership of fifty percent (50%) or more of the
      outstanding shares, or (iii) beneficial ownership of such entity.

      "You" (or "Your") shall mean an individual or Legal Entity
      exercising permissions granted by this License.

      "Source" form shall mean the preferred form for making modifications,
      including but not limited to software source code, documentation
      source, and configuration files.

      "Object" form shall mean any form resulting from mechanical
      transformation or translation of a Source form, including but
      not limited to compiled object code, generated documentation,
      and conversions to other media types.

      "Work" shall mean the work of authorship, whether in Source or
      Object form, made available under the License, as indicated by a
      copyright notice that is included in or attached to the work
      (an example is provided in the Appendix below).

      "Derivative Works" shall mean any work, whether in Source or Object
      form, that is based on (or derived from) the Work and for which the
      editorial revisions, annotations, elaborations, or other modifications
      represent, as a whole, an original work of authorship. For the purposes
      of this License, Derivative Works shall not include works that remain
      separable from, or merely link (or bind by name) to the interfaces of,
      the Work and Derivative Works thereof.

      "Contribution" shall mean any work of authorship, including
      the original version of the Work and any modifications or additions
      to that Work or Derivative Works thereof, that is intentionally
      submitted to Licensor for inclusion in the Work by the copyright owner
      or by an individual or Legal Entity authorized to submit on behalf of
      the copyright owner. For the purposes of this definition, "submitted"
      means any form of electronic, verbal, or written communication sent
      to the Licensor or its representatives, including but not limited to
      communication on electronic mailing lists, source code control systems,
      and issue tracking systems that are managed by, or on behalf of, the
      Licensor for the purpose of discussing and improving the Work, but
      excluding communication that is conspicuously marked or otherwise
      designated in writing by the copyright owner as "Not a Contribution."

      "Contributor" shall mean Licensor and any individual or Legal Entity
      on behalf of whom a Contribution has been received by Licensor and
      subsequently incorporated within the Work.

   2. Grant of Copyright License. Subject to the terms and conditions of
      this License, each Contributor hereby grants to You a perpetual,
      worldwide, non-exclusive, no-charge, royalty-free, irrevocable
      copyright license to reproduce, prepare Derivative Works of,
      publicly display, publicly perform, sublicense, and distribute the
      Work and such Derivative Works in Source or Object form.

   3. Grant of Patent License. Subject to the terms and conditions of
      this License, each Contributor hereby grants to You a perpetual,
      worldwide, non-exclusive, no-charge, royalty-free, irrevocable
      (except as stated in this section) patent license to make, have made,
      use, offer to sell, sell, import, and otherwise transfer the Work,
      where such license applies only to those patent claims licensable
      by such Contributor that are necessarily infringed by their
      Contribution(s) alone or by combination of their Contribution(s)
      with the Work to which such Contribution(s) was submitted. If You
      institute patent litigation against any entity (including a
      cross-claim or counterclaim in a lawsuit) alleging that the Work
      or a Contribution incorporated within the Work constitutes direct
      or contributory patent infringement, then any patent licenses
      granted to You under this License for that Work shall terminate
      as of the date such litigation is filed.

   4. Redistribution. You may reproduce and distribute copies of the
      Work or Derivative Works thereof in any medium, with or without
      modifications, and in Source or Object form, provided that You
      meet the following conditions:

      (a) You must give any other recipients of the Work or
          Derivative Works a copy of this License; and

      (b) You must cause any modified files to carry prominent notices
          stating that You changed the files; and

      (c) You must retain, in the Source form of any Derivative Works
          that You distribute, all copyright, patent, trademark, and
          attribution notices from the Source form of the Work,
          excluding those notices that do not pertain to any part of
          the Derivative Works; and

      (d) If the Work includes a "NOTICE" text file as part of its
          distribution, then any Derivative Works that You distribute must
          include a readable copy of the attribution notices contained
          within such NOTICE file, excluding those notices that do not
          pertain to any part of the Derivative Works, in at least one
          of the following places: within a NOTICE text file distributed
          as part of the Derivative Works; within the Source form or
          documentation, if provided along with the Derivative Works; or,
          within a display generated by the Derivative Works, if and
          wherever such third-party notices normally appear. The contents
          of the NOTICE file are for informational purposes only and
          do not modify the License. You may add Your own attribution
          notices within Derivative Works that You distribute, alongside
          or as an addendum to the NOTICE text from the Work, provided
          that such additional attribution notices cannot be construed
          as modifying the License.

      You may add Your own copyright statement to Your modifications and
      may provide additional or different license terms and conditions
      for use, reproduction, or distribution of Your modifications, or
      for any such Derivative Works as a whole, provided Your use,
      reproduction, and distribution of the Work otherwise complies with
      the conditions stated in this License.

   5. Submission of Contributions. Unless You explicitly state otherwise,
      any Contribution intentionally submitted for inclusion in the Work
      by You to the Licensor shall be under the terms and conditions of
      this License, without any additional terms or conditions.
      Notwithstanding the above, nothing herein shall supersede or modify
      the terms of any separate license agreement you may have executed
      with Licensor regarding such Contributions.

   6. Trademarks. This License does not grant permission to use the trade
      names, trademarks, service marks, or product names of the Licensor,
      except as required for reasonable and customary use in describing the
      origin of the Work and reproducing the content of the NOTICE file.

   7. Disclaimer of Warranty. Unless required by applicable law or
      agreed to in writing, Licensor provides the Work (and each
      Contributor provides its Contributions) on an "AS IS" BASIS,
      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
      implied, including, without limitation, any warranties or conditions
      of TITLE, NON-INFRINGEMENT, MERCHANTABILITY, or FITNESS FOR A
      PARTICULAR PURPOSE. You are solely responsible for determining the
      appropriateness of using or redistributing the Work and assume any
      risks associated with Your exercise of permissions under this License.

   8. Limitation of Liability. In no event and under no legal theory,
      whether in tort (including negligence), contract, or otherwise,
      unless required by applicable law (such as deliberate and grossly
      negligent acts) or agreed to in writing, shall any Contributor be
      liable to You for damages, including any direct, indirect, special,
      incidental, or consequential damages of any character arising as a
      result of this License or out of the use or inability to use the
      Work (including but not limited to damages for loss of goodwill,
      work stoppage, computer failure or malfunction, or any and all
      other commercial damages or losses), even if such Contributor
      has been advised of the possibility of such damages.

   9. Accepting Warranty or Additional Liability. While redistributing
      the Work or Derivative Works thereof, You may choose to offer,
      and charge a fee for, acceptance of support, warranty, indemnity,
      or other liability obligations and/or rights consistent with this
      License. However, in accepting such obligations, You may act only
      on Your own behalf and on Your sole responsibility, not on behalf
      of any other Contributor, and only if You agree to indemnify,
      defend, and hold each Contributor harmless for any liability
      incurred by, or claims asserted against, such Contributor by reason
      of your accepting any such warranty or additional liability.

   END OF TERMS AND CONDITIONS

   APPENDIX: How to apply the Apache License to your work.

      To apply the Apache License to your work, attach the following
      boilerplate notice, with the fields enclosed by brackets "[]"
      replaced with your own identifying information. (Don't include
      the brackets!)  The text should be enclosed in the appropriate
      comment syntax for the file format. We also recommend that a
      file or class name and description of purpose be included on the
      same "printed page" as the copyright notice for easier
      identification within third-party archives.

   Copyright 2018-2020 Deepak Kumar <deepak@kreatio.com>

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

## References

- [Spring Boot Docs](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [Spring JPA docs](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Thymeleaf Docs](https://www.thymeleaf.org/documentation.html)
- [Learn resources](https://learn.canterbury.ac.nz/course/view.php?id=13269&section=9)


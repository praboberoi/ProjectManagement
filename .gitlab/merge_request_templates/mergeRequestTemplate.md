#### The Title of the Merge Request.
##### This should be the name of the story or task it relates to and then where it's going i.e. "Create Group Functionality Task Merge Into Group Story Branch" rather than just the auto generated name.
**Description**\
This section should be a brief description of what the branch you are merging actually contains, i.e. This merge contains the back end functionality for adding groups as well as changes to the front end for the groups page. Make sure to also explicitly state the name of the branch being merged to help reviewers.

**Front End**\
If the merge contains code/changes related to the front end include a brief description accompanied by screenshots if needed and/or the url of the changed page. 

**Testing**\
Discuss what testing was carried out, if unit/acceptance give the names of the files containing the tests (as well as a brief description of the tests), if manual provide a link to the page containing the manual tests in the wiki as well as which tests they were.

**Relevant Wiki Pages**\
Manual testing pages should be covered above, however if there are other relevant wiki pages such as a spike or design decisions link them here.

**Issues**\
If there are any issues with the branch, leave a link to the issues here.

**Review Checklist**\
Feel free to cut non relevant items from this list.
- [ ] Each class should have a class docstring, and docstrings for every method except for getters or setters.
- [ ] These docstrings should have correct and up-to-date information about the method's parameters, exceptions, and return values.
- [ ] The code should be free from bugs (logical errors, syntax errors etc.)
- [ ] Feature merge request must be merged into a story.
- [ ] Story merge request must be merged into develop.
- [ ] Develop should be the sole branch merged into the master.
- [ ] The feature should meet the task DOD.
- [ ] The story should meet the story DOD.
- [ ] All tests pass.
- [ ] Relevant tests are created(refer to DOD).
- [ ] The pipeline passes.
- [ ] The changes should pass Sonarqube checks.
- [ ] Merge request should have a description of what it is for.
- [ ]  Regression testing is done for each story branch.


# Use Case: URL Shortener

At this point, we have a Springboot Application with a Service Layer and a Domain Model.

The service layer implements the business logic required. It uses a Memory Database for storing tokens and users.
There is no API. There are no Tests.

##Organisation

This branch have 3 groups of exercises: Testing, building a REST APi, and building Persistence using JPA.

###Exercises-base-2-test.md

Contains exercises for testing the Service Layer.

###Exercises-base-2-rest.md

Contains exercises for adding an API on top of the Service Layer.

###Exercises-base-2-jpa.md

Contains exercises for adding an persistence to MySql for the Service Layer.


## Reset branch

You can do all exercises in all three groups, and theoretically they should not disturp each other.

But if you find youself in a situation where you have lost the overview and just don't understand what is going on,
it might be time for a reset.

A reset will remove ALL changes made by you, so be careful using this approach!!

If you want to start over do this:

- Open a terminal (alt-F12)
- execute `git reset --hard`

This will restore all files to their original state from github.

You might still have added some files which you must delete by hand. You can get a list of these files by executing:

- `git status`

The files you added will be listed in the last section called "Untracked files". Delete these files.
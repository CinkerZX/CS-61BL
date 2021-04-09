# We are going to start from here **Gitlet.java**
### Write branch and rm-branch
### Gitlet.java try integration test on those existing methods by python + '.in' to debug add, remove, commit
### ? branchmanage.read method
### add the methods of reset, branchmanagement, checkout

### Special condition: users generate new sub-folders under the original working_directory (how to update the directory to the current one) 

# The structure of GITLET

### Object: Gitlet Repository
    //https://d1b10bmlvqabco.cloudfront.net/attach/k5eevxebzpj25b/jqr7jm9igtc7l5/k97ipfmgmb3n/Gitlet_Slides.pdf
    //https://cs61bl.org/su20/projects/gitlet/#the-commands
    //Testing: https://www.youtube.com/watch?v=ksY5s0nHU5I&feature=youtu.be
    MAIN:
    init //inititalize a repository folder under the directory of the object folder
    log // print out
    add // create the corresponding blobs; add an empty file with the name of 'sha1(blob)' into /staged for addition; 
    commit // construct the commit, accept the string text (messsage), update the pointers
    remove // create the corresponding blobs; add an empty file with the name of 'sha1(blob)' into /staged for removal ;
    global-log // print out all the commits
    find // find(Args)
    status // Check the tracked files(folder) change
            
    $$ to do $$ branch // Creates a new branch with the given name *java gitlet.Main branch [branch name]*
    $$ to do $$ rm-branch // Remove branch
    
    $$ to do $$ checkout // Update the current files in the current direction by *java gitlet.Main checkout -- [file name]*
                            *java gitlet.Main checkout [commit id] -- [file name]*
                            *java gitlet.Main checkout [branch name]*
    $$ to do $$ reset // Checks out all the files tracked by the given commit *java gitlet.Main reset [commit id]*
    
    $$ to do $$ merge
    $$ to do $$ rebase // *java gitlet.Main rebase [branch name]*
    
### Contents
    working directory
        .gitlet
            Staging Area
                Staged for addition: empty files with the name of 'sha1(blob)'
                Staged for removal: empty files with the name of 'sha1(blob)'
            Commits: files with the name of 'sha1(commit)', and the content of object 'commit'
            Blobs: files with the name of 'sha1(blob)', and the content of object 'blob'
    
#### Steps
* Create 3 subfolders: Staging Area, Commits, and Blods. // hide direction .gitlet
* Initialize the 0 commit object


### Object: Commit
* attributes
    - pa_sha: SHA1(previous commit) of the previous commit object
    - Metadata
        - message
        - timestamp
    - Files that tracked in the folder
        - Nb_commit: NBtable[ ] // Name-blob table
            - The full name of the file
            - The sha1(Blobs) of the file
* Commit 0: init
    - pa_sha: SHA1(previous commit) = NONE
    - Metadata
    - Nb_commit: NBtable[ ] = NONE
* Commit n
    - pa_sha: SHA1(previous commit)
    - Metadata
    - Nb_commit: NBtable[ ]
* Function
    - construction
        read the file name + blob name information from the staging area
    - findhead

### Object: branch_mani
* Attributes
    //- my_sha: SHA1(commit-self) //SHA = secure hash algorithm
    - NBtable[ ]
        - Name: the name of braches
        - Hash: SHA1(latest-commit)
    - head
        - Hash: NBtable //point out which branch we are in
* Function
    - update_head
    - update_branch

### Object: NBtable
* attributes
    - The full name of the file
    - The sha1(Blobs) of the file
* function
    - find: input = SHA1; output = true/false

### Object: Blobs
* attributes
    - Name: SHA1(the file name + the file content)
    - Content: output by java function ()

### Class: Main
* Attribute
* function
    - Add
        - generate SHA1(), check if the file has already exists, if no, do the following; if yes, do nothing about this 
          file
        - information in the Staging Area that which blob represents which file (after creat the blob for the file)
    - Creat blob
    - Create commit, update the NBtable of commit
    - log
        - Usage: java gitlet.Main log
    
                   ===
                   commit a0da1ea5a15ab613bf9961fd86f010cf74c7ee48
                   Date: Thu Nov 9 20:00:05 2017 -0800
                   A commit message.
          
                   ===
                   commit 3e8bf1d794ca2e9ef8a4007275acf3751c7170ff
                   Date: Thu Nov 9 17:01:33 2017 -0800
                   Another commit message.
          
                   ===
                   commit e881c9575d180a215d1a636545b8fd9abfb1d2bb
                   Date: Wed Dec 31 16:00:00 1969 -0800
                   initial commit
    - global-log
        - Usage: java gitlet.Main global-log
        - Description: Like log, except displays information about all commits ever made. The order of the commits does not matter.
    
    - reset
        - Usage: java gitlet.Main reset [commit id]

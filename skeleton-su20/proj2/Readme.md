# The structure of GITLET

### Object: Gitlet Repository

    MAIN:
    init //inititalize a repository folder under the directory of the object folder
    status // check the tracked files(folder) change
    add // create the corresponding blobs; add informations in the staging area
    remove // 
    commit // construct the commit, accept the string text (messsage)
    update the pointers
    log // 
    
    
#### Steps
* Create 3 subfolders: Staging Area, Commits, and Blods. // hide direction .gitlet
* Initialize the 0 commit object


### Object: Commit
* attributes
    - SHA1(commit-self) //SHA = secure hash algorithm
    - SHA1(previous commit) of the previous commit object
    - Metadata
        - message
        - timestamp
    - Files that tracked in the folder
        - NBtable[ ] // Name-blob table
            - The full name of the file
            - The sha1(Blobs) of the file
* Commit 0: init
    - SHA1(commit-self)
    - SHA1(previous commit) = NONE
    - Metadata
    - NBtable[ ] = NONE
* Commit n
    - SHA1(commit-self)
    - SHA1(previous commit)
    - Metadata
    - NBtable[ ]
* Function
    - construction
        read the file name + blob name information from the staging area
    - findhead

### Object: branch_mani
* Attributes
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

# The structure of GITLET

### Object: Gitlet Repository

    MAIN:
    init //inititalize a repository folder under the directory of the object folder
    
#### Steps
* Create 3 subfolders: Staging Area, Commits, and Blods.
* Initialize the 0 commit object


### Object: Commitnode
* attributes
    - SHA1(commit-self) //SHA = secure hash algorithm
    - SHA1(previous commit) of the previous commit object
    - Metadata
        - message
        - timestamp
    - Files that checked in the folder
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

### Object: branch_mani
    - NBtable[ ]
        - Name: the name of braches
        - Hash: SHA1(latest-commit)
    - head
        - Hash: NBtable //point out which branch we are in

### Object: NBtable
* attributes
    - The full name of the file
    - The sha1(Blobs) of the file
* function
    - find: input = SHA1; output = true/false

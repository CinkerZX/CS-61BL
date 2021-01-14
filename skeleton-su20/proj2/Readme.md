# We are going to start from here **Gitlet.java**
    //write the commit_0 object into the .commits folder
    //write the NBtable object
    //write the branch object

# The structure of GITLET

### Object: Gitlet Repository
    //https://d1b10bmlvqabco.cloudfront.net/attach/k5eevxebzpj25b/jqr7jm9igtc7l5/k97ipfmgmb3n/Gitlet_Slides.pdf
    //https://cs61bl.org/su20/projects/gitlet/#the-commands
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

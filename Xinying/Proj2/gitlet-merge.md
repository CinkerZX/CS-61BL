 *If there are staged **additions or removals present**, print the error message `You have uncommitted changes.`  **(F1)**and exit. If a branch with the given name does not exist, print the error message `A branch with that name does not exist.`**(F2)** If attempting to merge a branch with itself, print the error message `Cannot merge a branch with itself.(F3)` If merge would generate an error because the commit that it does has no changes in it, just let the normal commit error message for this go through.*



***find the nearest common ancestor of both branch (This is the Split Point)**

**If the split point is the latest commit of GB or CB,**

GB do nothing print "Given branch is an ancestor of the current branch."

CB checkout GB print "Current branch fast-forwarded."





Current branch == all files tracked till SP

Given branch == all files tracked till SP

Created/Modified/ Not Modified(Remained)/Deleted   (choose latest version)

Current branch(CB), Given branch(GB)

Split point(SP) 



 *If an untracked file in the current branch **would be overwritten or deleted by the merge**, print `There is an untracked file in the way; delete it, or add and commit it first.` **(F4)**and exit; perform this check before doing anything else.*





1. Any files that have been *modified* in the given branch since the split point, but not modified in the current branch since the split point should be changed to their versions in the given branch (checked out from the commit at the front of the given branch). These files should then all be automatically staged. To clarify, if a file is “modified in the given branch since the split point” this means the version of the file as it exists in the commit at the front of the given branch has different content from the version of the file at the split point.

   in SP

   M in GB

   NM in CB

   have the same BlobID in WD and CB ,otherwise, failure case 4 **F4**

   ----checkout to the version in GB and stage for add

2. Any files that have been modified in the current branch but not in the given branch since the split point should stay as they are.

   in SP

   M in CB

   NM in GB

   -----stay

3. Any files that have been modified in both the current and given branch in the same way (i.e., both to files with the same content or both removed) are left unchanged by the merge. If a file is removed in both, but a file of that name is present in the working directory that file is not removed from the working directory (but it continues to be absent<—>not staged<—>in the merge).

   M in both CB and GB with the same BlobID or D in GB and CB

   ---stay   

   if D in both GB and CB but present in WD, do nothing

4. Any files that were not present at the split point and are present only in the current branch should remain as they are.

   C in CB

   not in GB

   ----stay

5. Any files that were not present at the split point and are present only in the given branch should be checked out and staged.

   C in GB

   not in CB

   ---checkout and stage

6. Any files present at the split point, unmodified in the current branch, and absent in the given branch should be removed (and untracked).

   in SP

   NM in CB

   D in GB

   ----removed and untracked

7. Any files present at the split point, unmodified in the given branch, and absent in the current branch should remain absent.

   in SP

   NM in GB

   D in CB

   ----remain absent, (do nothing)

8. Any files modified in different ways in the current and given branches are *in conflict*. “Modified in different ways” can mean that the contents of both are changed and different from other, or the contents of one are changed and the other file is deleted, or the file was absent at the split point and has different contents in the given and current branches. In this case, replace the contents of the conflicted file with

- ```
        <<<<<<< HEAD
        contents of file in current branch
        =======
        contents of file in given branch
        >>>>>>>
  ```

  (replacing “contents of…” with the indicated file’s contents) and stage the result. **Treat a deleted file in a branch as an empty file. Use straight concatenation here.** In the case of a file with no newline at the end, you might well end up with something like this:

  ```
        <<<<<<< HEAD
        contents of file in current branch=======
        contents of file in given branch>>>>>>>
  ```

  This is fine; people who produce non-standard, pathological files because they don’t know the difference between a line terminator and a line separator deserve what they get.

  

  -- conflict

  M in CB and GB with different BlobID  or  

  C in CB and GB with different BlobID or

  M in CB and D in GB

  M in GB and D in CB ----have the same BlobID in WD and CB ,otherwise, failure case 4 **F4**

  --- replace the content and stage

  

*Once files have been updated according to the above, and the split point was not the current branch or the given branch, merge automatically commits with the log message `Merged [given branch name] into [current branch name].` Then, if the merge encountered a conflict, print the message `Encountered a merge conflict.` on the terminal (not the log). Merge commits differ from other commits: they record as parents both the head of the current branch (called the *first parent*) and the head of the branch given on the command line to be merged in.





There is one complication in the definition of the split point. You may have noticed that we referred to “a”, rather than “the” latest common ancestor. This is because there can be more than one in the case of “criss-cross merges”, such as this: ![Criss-Cross Merge](https://cs61bl.org/su20/projects/gitlet/image/crisscross2.png) Here, the solid lines are first parents and the dashed lines are the merged-in parents. Both the commits pointed by blue arrows above are latest common ancestors.

Here’s how this was created: Branch splits from master in the left most commit. We make initial commits in branch and in master. We then create a new branch temp, splitting from branch. Then we merge master into branch, creating the second bottom commit from the right. This also moves the head of branch forward. We make another commit in master, and then merge temp into master. Then, we make another commit in master and branch. Now if we want to merge branch into master, we have two possible split points: the commits marked by the two blue arrows. You might want to think about why it can make a difference which gets used as the split point. We’ll use the following rule to choose which of multiple possible split points to use: - Choose the candidate split point that is closest to the head of the current branch (that is, is reachable by following the fewest parent pointers along some path). - If multiple candidates are at the same closest distance, choose any one of them as the split point. (We will make sure that this only happens in our test cases when the resulting merge commit is the same with any of the closest choices.)



By the way, we hope you’ve noticed that the set of commits has progressed from a simple sequence to a tree and now, finally, to a full directed acyclic graph.
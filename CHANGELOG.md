# CHANGELOG

## 2017-05-20 - v1.0.0 1ea8b79

- Retcon the new revision history (#11)

### 2014-04-20 - v0.1.83 9de4882 convert all use of Selection[] to List<Selection>
### 2014-01-04 - v0.1.82 a3bc202 Merge pull request #3 from JosepFloriach/master
### 2013-09-22 - v0.1.81 ad23532 Merge pull request #2 from JosepFloriach/master
### 2013-05-13 - v0.1.80 46f88c4 commented new method
### 2013-04-21 - v0.1.79 2a8ab98 Allow max and min alter settings
### 2013-04-11 - v0.1.78 74d8e19 Option to ignore study ID mismatch some places
### 2013-04-03 - v0.1.77 a30cb3e Raw data CSV function now searches inside interview files
### 2013-03-28 - v0.1.76 a92306a Don't advance past last question in next unansrd.
### 2013-02-21 - v0.1.75 9b47f24 Next unanswered question button
### 2013-02-13 - v0.1.74 27f9b4a shift-down arrow now moves a question to the end
### 2013-02-06 - v0.1.73 87253e3 adding LICENSE and README content
### 2012-11-25 - v0.1.72 87253e3 adding LICENSE and README content
### 2011-03-31 - v0.1.71 cfd4789 Fixed the failing test case mentioned in previous commit. When deciding whether a node could be added, I was checking whether the existing members would be put over their limit of missing edges, but not whether the new member would be over its limit.
### 2011-03-30 - v0.1.70 188d7a6 Whole network export labels nodes with mapping group id, rather than name, and makes name one of the attributes in the alter attribute export.
### 2011-03-08 - v0.1.69 e117577 Researcher can decide whether ego should always be tied to their own alters, or whether an ego-alter relationship is worth one vote in discrepancy resolution just like any other tie data.
### 2011-03-04 - v0.1.68 769054b Improvement to consensus data writer. Instead of taking all alters, and finding reporters that can report on all of them (there usually turn out to be no reporters), search for a subset of alters for which there are reporters who can report on all of them. Find such a subset which maximizes ((number of alters) CHANGELOG.md EgoNet Graphing Structure.odg LICENSE.md README.md bin build-launch4j.xml build.sbt circle.yml date-releases.md date2 fix-dates.sh lib project resources src target winwrap.xml (number of reporters) CHANGELOG.md EgoNet Graphing Structure.odg LICENSE.md README.md bin build-launch4j.xml build.sbt circle.yml date-releases.md date2 fix-dates.sh lib project resources src target winwrap.xml minimum((number of alters),(number of reporters)).
### 2011-03-03 - v0.1.67 b9f9b2f New discrepancy strategy, in which we completely ignore alter pair questions and tie people based only on one of them being a respondent (ego) and mentioning the other.
### 2011-02-24 - v0.1.66 1c3ce96 Improving consistency between wholenet alter attributes output and wholenet adjacency matrix output. When sanitizing alter names, two disallowed characters in a row should be replaced by one underscore, not two.
### 2011-02-03 - v0.1.65 c842261 Reduce the file size of whole network edgelist by skipping pairs that aren't connected.
### 2011-02-01 - v0.1.64 5967783 When deciding whether two whole network nodes are tied, using the majority discrepancy resolution strategy, if there is an ego-alter relationship between those nodes then they are tied, ignoring any other information to the contrary.
### 2011-01-31 - v0.1.63 5967783 When deciding whether two whole network nodes are tied, using the majority discrepancy resolution strategy, if there is an ego-alter relationship between those nodes then they are tied, ignoring any other information to the contrary.
### 2011-01-25 - v0.1.62 1c54d56 Removed text variables from tie data in VNA export format.
### 2011-01-21 - v0.1.61 6b7ef2e Export interview data in VNA format as part of "Save Summary Statistics" operation for later import into UCINet or NetDraw.
### 2011-01-14 - v0.1.60 673133a Consistent representation of alter names in output formats.
### 2011-01-12 - v0.1.59 4a6e0dc A node label should be directly to the right of its node, not diagonally down and right.
### 2011-01-05 - v0.1.58 6eb5080 Cursor looks like an hourglass during whole network automatching. Prior to this change, it was difficult to see whether automatching had finished (and Egonet appeared to be frozen).
### 2011-01-03 - v0.1.57 abdb099 Consistent representation of alter names in whole network output formats.
### 2010-12-23 - v0.1.56 f34c2c7 Export alter attributes from whole network analysis viewer.
### 2010-09-30 - v0.1.55 043b425 Increase maximum heap size in the Windows wrapper to prevent out-of-memory errors.
### 2010-07-23 - v0.1.54 0007a17 Raw data CSV output should always have a CSV extension, even if the user did not specify an extension. Also, if the user cancels the file selection dialog, Egonet should do nothing rather than throw an error.
### 2010-06-18 - v0.1.53 8e933d9 The "Save Summary Statistics" button should export alter summaries, in addition to the adjacency matrices that it already exports.
### 2010-05-18 - v0.1.52 c8ce825 When summary statistics generation fails for an interview, the error message should indicate which interview had a problem.
### 2010-05-12 - v0.1.51 263de30 Labels for whole network adjacency matrix should not include alter IDs.
### 2010-05-06 - v0.1.50 b788751 Added labels to whole network adjacency matrix.
### 2010-05-05 - v0.1.49 db876ee Added an 'inclusion threshold' setting to the whole network analysis section. A node in the whole network is included only if that node was mentioned in some minimum number of interviews.
### 2010-03-30 - v0.1.48 e066b60 Fixed two bugs in the whole network alter name mapping editor.
### 2010-02-17 - v0.1.47 ebcb537 Allow sorting by alter name or ego name in whole network alter name mapping editor.
### 2009-12-11 - v0.1.46 8e08da5 Alter name mappings for whole network analysis can be saved.
### 2009-12-08 - v0.1.45 5e6d7cb Added answers for alter questions to the alter name mapping editor so that it is easier to determine whether alters from different interviews are the same person.
### 2009-11-11 - v0.1.44 a52a5f0 If there is more than one alter prompt, it should be possible to pass through the earlier prompts without entering all of the alters.
### 2009-10-04-BETA4 - v0.1.43 d6f817b - Added a button to reset all graphing parameters - Catch null pointers in empty names on whole network matching
### 2009-09-27-BETA3 - v0.1.42 816f26b - Bugfix: Couldn't select the last answer for certain linked question dialogs where the answers appeared in a drop down. - Bugfix: If the statistics folder isn't present when a survey isn't completed, try to create it and log any errors in a better way.
### 2009-09-20-BETA2 - v0.1.41 816f26b - Bugfix: Couldn't select the last answer for certain linked question dialogs where the answers appeared in a drop down. - Bugfix: If the statistics folder isn't present when a survey isn't completed, try to create it and log any errors in a better way.
### 2009-09-15-BETA - v0.1.40 816f26b - Bugfix: Couldn't select the last answer for certain linked question dialogs where the answers appeared in a drop down. - Bugfix: If the statistics folder isn't present when a survey isn't completed, try to create it and log any errors in a better way.
### 2009-09-15 - v0.1.39 816f26b - Bugfix: Couldn't select the last answer for certain linked question dialogs where the answers appeared in a drop down. - Bugfix: If the statistics folder isn't present when a survey isn't completed, try to create it and log any errors in a better way.
### 2009-09-12 - v0.1.38 5d23528 - Alter prompt / name generator window layout improved. - Starting an interview's usability improved, smarter focus on controls. - Interview / analysis initial UI layout improved.
### 2009-07-19 - v0.1.37 8ceca98 Log more about files, and use all lower case when doing name similarities.
### 2009-07-13 - v0.1.36 b31215c Fixed whole network analysis by finding a couple more places where a first name and last name were converted into a string, incorrectly handling null values. Used the same fix as in revision [182] delegating this task to org.egonet.util.Name.toString().
### 2009-07-10 - v0.1.35 9dcb9a0 Changed the "Summary Statistics" button on the main menu to "Save Summary Statistics" and changed its behavior to immediately open a "save as..." file dialog rather than showing a panel with a "save statistics" option.
### 2009-07-08 - v0.1.34 6cdd359 Bug fix: It was not possible to enter any alter names for alter prompt question if alters were entered as a single name (rather than the old first/last name) and question skipping was allowed. This is a conflict between the implementations of entering alters as a single name () and allowing question skipping (revision 172). This is only a partial fix, as it does not allow an interviewer to enter less than the expected number of alters at the alter prompt.
### 2009-06-25 - v0.1.33 c92192e Fixes an issue with the new JUNG 2 wrapping layouts inside a delegate class.
### 2009-05-30 - v0.1.32 3c2941c - New notes tab during an interview for taking notes about the task / respondent / etc - New HTML support for questions; try "<html>Some text in <b>bold<b>" and other formatting. - New setting on a study that allows an interview's questions to be skipped at any time
### 2009-05-09 - v0.1.31 7a6e210 Added export formats to whole network analysis
### 2009-05-02 - v0.1.30 d5871bf Completed a very basic whole network construction for the new whole network analysis component.
### 2009-04-20 - v0.1.29 1bda765 Fixed a bad/incorrect import
### 2009-04-17 - v0.1.28 a43fca3 Updated build scripts for new jung
### 2009-03-20 - v0.1.27 4977874 Removed the copy-to-sourceforge target.
### 2009-03-18 - v0.1.26 84f6fcb Build script error in a property name caused Main-Class not to be correct.
### 2009-03-13 - v0.1.25 5c43eaf Removed unused file.
### 2009-03-09 - v0.1.24 b44ca6d First answer in a categorical question with drop-down didn't count as "answered" when it should!
### 2009-02-01 - v0.1.23 b44ca6d First answer in a categorical question with drop-down didn't count as "answered" when it should!
### 2009-01-30 - v0.1.22 95d5b3c Fixed question import bug, added better node coloring algorithm.
### 2008-12-13 - v0.1.21 0fb38bb Added saving data as coordinates or edgelist.
### 2008-11-15 - v0.1.20 3577e06 Can't write alter names as XML comments if we don't have them yet!
### 2008-11-11 - v0.1.19 93e29e6 Comments cleanup
### 2008-11-10 - v0.1.18 dad601f Adding an ant target for SF release
### 2008-11-06 - v0.1.17 2b74f24 Quick wording change in the about box
### 2008-10-06 - v0.1.16 3301ef6 Added a condition where the canvas will not resize if the canvas size will become smaller than 5.
### 2008-09-30 - v0.1.15 a04b714 Added functionality to the change layout size buttons
### 2008-09-29 - v0.1.14 a444e90 Fixing some poor handling of 2 dimensional arrays (weird initialization then overwrite, lack of checking for null q.answerType in members), adding error message dialog for SwingWorker instances so they don't silently eat the error.
### 2008-09-22 - v0.1.13 b51c111 Attempted to fix categorical list form layout. Added a question duplication button.
### 2008-08-04 - v0.1.12 e54b98c Replaced elsutils.jar with the actual source, released by Peter.
### 2008-07-11 - v0.1.11 5a34d2f Alter list caps at max size, if you specify one.
### 2008-07-10 - v0.1.10 c07a86d Update to fix nodes until Sowmya does her fix that un-deletes my original working node-coloring.
### 2008-07-07 - v0.1.9 0ad9111 Load/apply was filtering out files that didn't end in .settings -- this was a mistake, since any of our settings file should end in .xml, and be loadable with a filter that shows .xml files.
### 2008-07-01 - v0.1.8 f7a2cba I overlooked the changes Martin made - Removed applygraphsetting file and added the functionality to loadSettingfile method
### 2008-06-10 - v0.1.7 0083f16 Minor change to Graph save option - Also test to see if I could commit.
### 2008-06-01 - v0.1.6 4c34175 Saving images works better now.
### 2008-04-15 - v0.1.5 ad2932f Added "Text" type questions also to graph capabilities.
### 2008-04-10 - v0.1.4 b5f544f To add separate frames for properties
### 2008-03-27 - v0.1.3 a144eb0 Window size adjusted to avoid "Make adjacent" button from being hidden
### 2008-03-25 - v0.1.2 29c13d3 1) Set default indices for categorical questions while taking interview 2) Added zoom in and out buttons for graph 3) Display alter names instead of alter numbers while viewing interviews
### 2008-03-11 - v0.1.1 894eb6e It helps to include the new libraries in the build.
### 2008-03-03 - v0.1.0 8b7558b Moving some of our reworked and local work into the repository

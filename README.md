# Scala Text Search
A CLI application that searches a given directory for text files 
and then accepts queries for words found in their contents.

Results are returned as a percent score of the queried words matched 
for up to 10 highest scoring files, ordered by score.

Queries with multiple instances of the same word are accepted (ex.: "tomato tomato some some").

#### Assumptions:
 - Files are encoded in UTF-8 or ISO-8859-1. If an unsupported character is detected, an error message will be displayed.
   - However, please note that an unsupported filetype can be read as a series of supported characters and produce incorrect results.
 - Words are case-insensitive sequences of letters, each separated by up to one consecutive hyphen or apostrophe.
   - Ex.: "some", "I've", "state-of-the-art".
 - Letters are unicode characters in the unicode blocks "Basic Latin" or "Latin-1 Supplement", 
   more specifically any letter matching the regex [a-zA-ZÀ-ÖØ-öø-ÿ].
   - Ex.: "a", "M", "ç", "Æ", "ß"




## Instructions

The program can be started from the SearchMain object in the package textsearch. The first argument should be the path to a directory containing text files.
Example below:

```
> sbt
> runMain textsearch.SearchMain directoryContainingTextFiles
```
A list of words can then be provided:

```
search> query words go here
```
Finally, a result of up to 10 most relevant files is provided as per the example below:
 ```
[100.0 %]: vol09.iss0050-0100.txt
[100.0 %]: vol08.iss0001-0071.txt
[100.0 %]: vol04.iss0064-0118.txt
[75.0 %]: tr823.txt
[75.0 %]: adventur.txt
[50.0 %]: intro.txt
[50.0 %]: howtobbs.txt
[50.0 %]: 203.txt
[25.0 %]: ethics.txt
[25.0 %]: codegeek.txt
 ```

## Future Work

- Matching accented characters (éãö...)
- Reporting conditions after indexing, ex.: warning if some .txt file could not be read (currently, inaccessible files are ignored)
- Expanding the testing suite
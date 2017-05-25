  String ツ = "\uD83D\uDE00";

# In Progress

* javaHtml as Plugin using fluent-html-api

# Refactorings
* FileController and Tests

# Next Steps

* `api.raml` füer `/fs/` und `/fb/`?
* return (jackson?-auto-mapped) `.json` for directories with `/fs/`

# Rendering
## Directory Rendering
1. file index.* (any target-mimetype-converter extension)
2. file <dirname>.* (any target-mimetype-converter extension)
3. Default Directory Render for target-mimetype extension
## File Rendering
1. any target-mimetype converter
2. fallback: file with own mime type

# Metadata
## Directory metadata (which renderer? what encoding?)
1. index.yml
2. <dirname>.yml
## File metadata (enforce mime type, e. g. atom.xml, text-xml.xml, application-text.txt)
1. front matter (markdown, asciidoc)
   * Asciidoctor front matter
     * ! skip-front-matter http://asciidoctor.org/docs/user-manual/#front-matter-added-for-static-site-generators
     * ? pass through front-matter https://github.com/asciidoctor/asciidoctor/issues/915
#2. <filename>.yml

## Plugin API
* http://stackoverflow.com/a/15428749/650411
** remiove need for Service annotation (spring-context dependency)...

## -dev-profile & config documentation

## Include / Exclude

* hide dot files (regex)
* absolute box paths

## "Integration"

* index.html/.md/.adoc
* [CalDAV](https://en.wikipedia.org/wiki/CalDAV)
* [CarddDAV](https://en.wikipedia.org/wiki/CardDAV)
* [Atom Syndication Format](ihttps://en.wikipedia.org/wiki/Atom_(standard)) 
* https://zapier.com/blog/plain-text-files-for-productivity/
* [todo.txt](http://todotxt.com/) 
  * https://github.com/ginatrapani/todo.txt-cli/wiki/The-Todo.txt-Format
  * https://github.com/ginatrapani/todo.txt-android
  * https://github.com/ginatrapani/todo.txt-cli
* alt: [taskpaper](https://www.taskpaper.com/)

### Research
* [VooDooPad Special Linking](http://tagamac.com/2009/09/scratchpad_part2/)

## Meta

* Unit-Test for Error Handler
* License selection
* Version scheme
* Coding style: https://google.github.io/styleguide/javaguide.html

## Formats

### Binary
* epub
* pdf
* 


### Publican / DocBook

* https://jfearn.fedorapeople.org/en-US/Publican/4.3/html/Users_Guide/index.html
* http://shakthimaan.com/downloads/glv/presentations/DocBook_publican_2012.pdf

### Java

* http://stackoverflow.com/questions/1853419/syntax-highlighter-for-java

### Asciidoctor
https://github.com/asciidoctor/asciidoctorj
http://discuss.asciidoctor.org/

* TODO without external references (fonts.googleapis.com)
** http://discuss.asciidoctor.org/Using-Font-Awesome-offline-td1831.html
* asciidoctor->pdf
* asciidoctor->epub3

* md/adoc with embedded images, includes and maybe dialects
* Trigger conversion by 2nd suffix (song.mp3.txt) in addition to accept header

* html->pdf using https://github.com/danfickle/openhtmltopdf
* text->pdf using pdfbox
** https://pdfbox.apache.org/[Apache PDFBox]
* pdf->text using tika (w/ pdfbox)

* html->txt using tika (w/ tagsoup)

* rtf & doc/xsl/ppt & odt/ods/odp -> txt using tika (using Apache POI and...)
** https://dzone.com/articles/using-apache-poi-to-read-excel

* dir|file -> zip using java

### CSV

* csv->html using univocity-parsers and custom rendering
** https://github.com/uniVocity/univocity-parsers[univocity-parsers] (csv)
* csv/xls with Apache POI
* https://super-csv.github.io/super-csv/index.html
* barchart geeration sample: http://www.d3-generator.com/

### Markdown

* https://github.com/vsch/flexmark-java/wiki/Usage

### Flat files

* http://flatworm.sourceforge.net/

## Online Service Integration

* Speech to Text
* Text to Speech
* Automatic translation using web service
  * [Tika Parsing](https://tika.apache.org/1.8/examples.html#Parsing_using_the_Tika_Facade)
  * [Tika Translation](https://tika.apache.org/1.8/api/org/apache/tika/language/translate/Translator.html)
  * [Tika and Microsoft](https://tika.apache.org/1.8/examples.html#Parsing_using_the_Tika_Facade)

## Eventually

* integrate/sync/backup (API-gateway?) with external services (storage, bookmarks, notes etc.)
** Simplenote, Pinboard, Github gists
* Authentication & Encryption
* Java 9 w/ modules
* diff-servie using e. g. https://diffoscope.org/
* image conversion

# Ideas

* Content adressing
** `git cat-file -p fa9296bc9428a251d92f6dfd1de20cf138addf8b`
* URIs to survive document renaming
** use history and send "moved permanently"
** use IDs for addressing and allow titles as unused comments
*** domain.tld/parent/permament-techkey/-optional-businesskey-title-which-is-ignored

## Notes / References

### Asciidoctor

* http://asciidoctor.org/docs/user-manual/#elements

### HTTP
* [HTTP Header](https://www.tutorialspoint.com/http/http_header_fields.htm)
* https://en.wikipedia.org/wiki/List_of_HTTP_header_fields
* [Linkl, alternate](https://www.w3.org/2001/tag/doc/alternatives-discovery.html)
* HTTP/1.1 default encoding is ISO-8859-1.

### Redirects

http://www.baeldung.com/spring-redirect-and-forward

response.sendRedirect("some-url"); // TODO: forward, not rediret
// http://www.baeldung.com/spring-redirect-and-forward
//                return htmlService.listDireectory(filePath, demo, boxPath, request.getContextPath());

### Server

[Tomcat vs. Jetty vs. Undertow](https://examples.javacodegeeks.com/enterprise-java/spring/tomcat-vs-jetty-vs-undertow-comparison-of-spring-boot-embedded-servlet-containers/)

### Spring Boot Actuator

* http://javabeat.net/spring-boot-actuator/
* http://www.baeldung.com/spring-boot-actuators

### Deplioyment / Build / Distribution
* https://maven.apache.org/plugins/maven-shade-plugin/examples/includes-excludes.html
* https://dzone.com/articles/packaging-springboot-application-with-external-dep

# Configuration

## Implementation

[spring boot property validation sample](https://github.com/spring-projects/spring-boot/tree/v1.5.2.RELEASE/spring-boot-samples/spring-boot-sample-property-validation)

## Testing

[how to test classes with configurationproperties](http://stackoverflow.com/questions/31745168/how-to-test-classes-with-configurationproperties-and-autowired)

[spring](https://hoserdude.com/2014/06/19/spring-boot-configurationproperties-and-profile-management-using-yaml/)

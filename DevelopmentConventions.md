# Introduction #

Cadmos development follows a number of conventions that are documented in this page.

# Format #
If you have a properly set-up development environment (see DevelopmentEnvironmentSetup) most format issues are automatically handled by eclipse.

Eclipse's automatic code formatter and save actions are enabled for each project in the background by using a common `./settings` directory.
This directory is included by `svn:externals` and formatter and save actions are maintained in the **default-project**.

Here are some key points concerning the format:
  * Blank line before field or method declaration.
  * Always use blocks `{...`} in `for`, `while` and `if` statements.
  * Use `final` for fields and local varaibles where possible.
  * Remove unnecessary casts.
  * Remove trailing whitespace from all lines.

# Naming #
  * Project names follow these patterns:
    * `edu.tum.cs.cadmos.<...>.core` for the _"console only"_ Java library part of a project, providing core functionality. Such a _"core project"_ solely has a `META_INF/MANIFEST.MF` file.
    * `edu.tum.cs.cadmos.<...>.ui` for the _"RCP/SWT"_ plug-in part of a project, provising a user interface for the functionality implemented in the _"core project"_. Such a _"ui project"_ additionally has a `plugin.xml` file to define views, extensions points etc.
  * Type names have the following conventions:
    * Class names are combinations of nouns, while each noun starts with an uppercase letter, e.g. `Channel` and `AtomicComponent`.
    * Interface names follow the conventions of class names and additionally start with the letter "I", e.g. `IVariable` and `ICompositeComponent`.
    * Enumeration names follow the conventions of class names and additionally start with the letter "E", e.g. `EType` and `EOperator`.
    * Class names of abstract classes follow the conventions of class names and additionally start with the prefix "Abstract", e.g. `AbstractComponent` and `AbstractTypedElement`.
  * Members of types follow these naming conventions:
    * Field names are a combination of words (usually nouns and adjectives). A field name starts with a word in lowercase letters, while all following words start with an uppercase letter, e.g. `children` and `leftOperand`. Names of boolean fields are often adjectives, e.g. `enabled`.
    * Methods names start with a lowercase verb, e.g. `getIncoming()`.
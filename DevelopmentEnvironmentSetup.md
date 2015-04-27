# Introduction #
The target audience of this page are developers that extend and maintain the Cadmos code base.
This page describes the steps to set up a working development environment:
  1. Prerequisites
  1. Eclipse
  1. Workspace
  1. Subclipse <a href='Hidden comment:  # Quality Rating '></a>
  1. SWT Designer
  1. SVN Auto-Properties
  1. Source Check-out


# Prerequisites #
Cadmos is developed using new Java 7 language features like inferred type parameters (e.g `Set<String> names = new HashSet<>();`), so you need a Java 7 runtime or SDK on the development machine, e.g. available at http://www.oracle.com/technetwork/java/javase/downloads/index.html .

<a href='http://www.oracle.com/technetwork/java/javase/downloads/index.html'><img src='http://www.oracle.com/ocom/groups/public/@otn/documents/webcontent/347320.gif' /></a>


# Install Eclipse 4.3 (including Xtext) #
Cadmos makes use of Xtext and Java 7 language features
which is fully supported starting from Eclipse version 3.7.1.
A pre-built Eclipse 4.3 release including all necessary Xtext features can be downloaded and installed for many platforms and operating systems from http://www.eclipse.org/Xtext/download.html (**Full Eclipse** recommended).

<a href='Hidden comment: 
<a href="http://www.eclipse.org/Xtext/download.html">http://www.eclipse.org/eclipse.org-common/themes/Nova/images/eclipse.png

Unknown end tag for &lt;/a&gt;


'></a>
<a href='http://www.eclipse.org/Xtext/download.html'><img src='http://www.eclipse.org/Xtext/images/logo.png' /></a>

# Setup Eclipse Workspace #
Start eclipse and choose a workspace to work with.
In the menu, choose _Window ... Preferences ... General ... Workspace_ and select **UTF-8** as _Text file encoding_.

![http://www4.in.tum.de/~schwitze/cadmos/utf8.png](http://www4.in.tum.de/~schwitze/cadmos/utf8.png)

# Install Subclipse #
Cadmos uses SVN for version control, which is supported by several client integrations for eclipse.
We use a eclipse SVN client called **subclipse**.

You can use this eclipse update manager url to install subclipse 1.10.x: http://subclipse.tigris.org/update_1.10.x

<a href='http://subclipse.tigris.org/'><img src='http://subclipse.tigris.org/branding/images/logo.gif' /></a>

<a href='Hidden comment: 
= Install ConQAT Rating Support =
Cadmos uses a review based quality rating (*RED*, *YELLOW* or *GREEN*) for each file:
* *RED:* File is under development or has pending todos after review.
* *YELLOW:* File is marked _"ready for review"_ by developer.
* *GREEN:* File passed review.
This quality rating is tool-supported for eclipse by the *ConQAT rating support*, which can be installed by the eclipse update manager, using the url: [http://www4.in.tum.de/~ccsm/eclipse_update_site]

For our purposes, it is sufficient to select solely the plug-in *"ConQAT Development Tools"* in the eclipse update manager.

<a href="http://conqat.cs.tum.edu/index.php/ConQAT">http://conqat.informatik.tu-muenchen.de/images/2/28/Conqat_logo_wide_tum.jpg

Unknown end tag for &lt;/a&gt;


'></a>

# Install SWT Designer #
**Skip this step** if you are using a pre-built **Full Eclipse** version from http://www.eclipse.org/Xtext/download.html, since this already includes _SWT Designer_.

Most developers also work on user interface related code, which is developed in Cadmos using the _SWT Designer_.
Use the eclipse update manager, go to the pre-installed _Juno_ update site and open the node _General Purpose Tools_.
Select _SWT Designer_ and _SWT Designer Core_ here and proceed with download and installation.

![http://code.google.com/javadevtools/wbpro/preferences/images/flyout_palette1.png](http://code.google.com/javadevtools/wbpro/preferences/images/flyout_palette1.png)


# Enable SVN Auto-Properties #
In your system's SVN **config** file, some SVN-properties need to be added in the `[auto-props]` section:

  * Make a backup copy of the original config file.
  * Open the config file with a text editor (e.g. notepad).
  * Go to the `[auto-props]` section.
  * Add the entry `*.java = svn:mime-type=text/plain;svn:eol-style=native;svn:keywords=Date Author Id Revision HeadURL`
  * Save the config file.

This automatically adds the SVN-properties (mime-type, eol-style, keywords) to new java-files when these are commited for the first time.

By convention we use the keywords $Author: $ and $Rev: $ in header comments of Cadmos java-files, to automatically include the latest author and revision of a java-file on commit.

On Windows systems, the SVN config file is typically found in a path similar to `*...\<user>\AppData\Roaming\Subversion*`.

# Check-out the Workspace Projects #
  * Follow the guidelines on http://code.google.com/p/cadmos/source/checkout to get read-only or write access to the source.
  * Build the language infrastructure (see below, takes about 5-10 seconds):
![http://www4.in.tum.de/~schwitze/cadmos/gen-lang-is.png](http://www4.in.tum.de/~schwitze/cadmos/gen-lang-is.png)

# That's it! #
You may go on with
  * Reading the page WorkspaceProjectsOverview that helps you to decide, which projects you need to check-out into your eclipse workspace.
  * Reading the DevelopmentConventions for Cadmos developers.
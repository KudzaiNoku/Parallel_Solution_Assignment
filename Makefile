JAVAC = /usr/bin/javac
JAVA = /usr/bin/java

.SUFFIXES: .java .class
SRCDIR = src
BINDIR = bin
DOCDIR = doc

$(BINDIR)/%.class: $(SRCDIR)/%.java
	$(JAVAC) -d $(BINDIR)/ -cp $(BINDIR) $<

CLASSES = MonteCarloMini.TerrainArea.class \
	MonteCarloMini.Search.class \
	MonteCarloMini.MonteCarloMinimizationParallel.class
  
CLASS_FILES = $(CLASSES:%.class=$(BINDIR)/%.class)

default: $(CLASS_FILES)

clean:
	rm $(BINDIR)/*.class

run: $(CLASS_FILES)
	$(JAVA) -cp $(BINDIR) MonteCarloMini.MonteCarloMinimizationParallel $(ARGS)
MAKEFLAGS = -s

rsync = rsync -rz

build = build
out = _site
work = ../
tmp = /var/tmp

version = 1.2.0

gh-pages = ../gh-pages
avian-web = ../readytalk.github.io/avian-web
web-host = http://jdpc.ecovate.com:8080/avian-web

proguard-version = 4.11
swt-version = 4.3
lzma-version = 920

programs = example

swt-zip-map = \
	linux-x86_64:swt-$(swt-version)-gtk-linux-x86_64.zip \
	linux-i386:swt-$(swt-version)-gtk-linux-x86.zip \
	linux-arm:swt-$(swt-version)-gtk-linux-arm.zip \
	linux-arm64:swt-$(swt-version)-gtk-linux-arm64.zip \
	macosx-x86_64:swt-$(swt-version)-cocoa-macosx-x86_64.zip \
	macosx-i386:swt-$(swt-version)-cocoa-macosx.zip \
	windows-x86_64:swt-$(swt-version)-win32-win32-x86_64.zip \
	windows-i386:swt-$(swt-version)-win32-win32-x86.zip

test-host-map = \
	linux-x86_64:$(USER):localhost:22 \
	linux-i386:$(USER):localhost:22 \
	linux-arm:$(USER):localhost:5556 \
	linux-arm64:$(USER):localhost:7777 \
	macosx-x86_64:joel.dice:192.168.50.40:22 \
	macosx-i386:joel.dice:192.168.50.40:22 \
	windows-x86_64:Joel:192.168.50.38:22 \
	windows-i386:Joel:192.168.50.38:22

build-host-map = \
	linux-x86_64:$(USER):localhost:22 \
	linux-i386:$(USER):localhost:22 \
	linux-arm:$(USER):localhost:22 \
	linux-arm64:$(USER):localhost:22 \
	macosx-x86_64:joel.dice:192.168.50.40:22 \
	macosx-i386:joel.dice:192.168.50.40:22 \
	windows-x86_64:$(USER):localhost:22 \
	windows-i386:$(USER):localhost:22

platforms = $(sort $(foreach x,$(swt-zip-map),$(word 1,$(subst :, ,$(x)))))

examples = $(foreach x,$(platforms),$(build)/$(x)-example.d)
tests = $(foreach x,$(platforms),$(build)/$(x)-test.d)
ci-tests = $(foreach x,$(platforms),$(build)/$(x)-ci.d)
get-platform = $(word 1,$(subst -, ,$(1)))
get-arch = $(word 2,$(subst -, ,$(1)))
get-subplatform = $(word 3,$(subst -, ,$(1)))
full-platform = $(shell echo $(1) | sed 's^$(build)/\(.*\)-.*.d^\1^')
arch = $(call get-arch,$(call full-platform,$(1)))
platform = $(call get-platform,$(call full-platform,$(1)))
subplatform = $(call get-subplatform,$(call full-platform,$(1)))
extension = $(if $(filter windows,$(call platform,$(1))),.exe)
map-value = $(patsubst $(1):%,%,$(filter $(1):%,$(2)))
map-value1 = $(shell echo $(call map-value,$(1),$(2)) | sed 's^\(.*\):.*:.*^\1^')
map-value2 = $(shell echo $(call map-value,$(1),$(2)) | sed 's^.*:\(.*\):.*^\1^')
map-value3 = $(shell echo $(call map-value,$(1),$(2)) | sed 's^.*:.*:\(.*\)^\1^')
test-user = $(call map-value1,$(call full-platform,$(1)),$(test-host-map))
test-host = $(call map-value2,$(call full-platform,$(1)),$(test-host-map))
test-port = $(call map-value3,$(call full-platform,$(1)),$(test-host-map))
build-user = $(call map-value1,$(call full-platform,$(1)),$(build-host-map))
build-host = $(call map-value2,$(call full-platform,$(1)),$(build-host-map))
build-port = $(call map-value3,$(call full-platform,$(1)),$(build-host-map))
swt-zip = $(call map-value,$(call full-platform,$(1)),$(swt-zip-map))
windows-git-clone = \
	git clone https://github.com/readytalk/win64.git || (cd win64 && git pull); \
	git clone https://github.com/readytalk/win32.git || (cd win32 && git pull);
git-clone = $(if $(filter windows,$(call platform,$(1))),$(call windows-git-clone,$(1)))
tmpdir = $(tmp)/$(USER)-avian-$(call full-platform,$(1))

.PHONY: all
all: $(results) $(extra_results)

.PHONY: deploy
deploy: deploy-pages

.PHONY: deploy-pages
deploy-pages: $(out)/index.html
	cp -a $(out)/* $(gh-pages)/

.PHONY: build-examples
build-examples: $(examples)

.PHONY: test
test: $(tests)

.PHONY: ci
ci: $(ci-tests)

.PHONY: deploy-examples
deploy-examples: build-examples
	cp -a $(build)/swt-examples $(avian-web)/

.PHONY: deploy-avian
deploy-avian:
	(cd $(work)/avian && make version=$(version) tarball && ./gradlew javadoc)
	cp -a $(work)/avian/build/avian-$(version).tar.bz2 $(avian-web)/
	mkdir -p $(avian-web)/javadoc-$(version)/
	cp -a $(work)/avian/build/docs/javadoc/* $(avian-web)/javadoc-$(version)/
	(cd $(work)/avian-swt-examples && make version=$(version) tarball)
	cp -a $(work)/avian/build/avian-swt-examples-$(version).tar.bz2 $(avian-web)/

build-sequence = \
	set -e; \
	rm -rf $(call tmpdir,$(1)); \
	mkdir -p $(call tmpdir,$(1)); \
	cd $(call tmpdir,$(1)); \
	curl -Of $(web-host)/$(call swt-zip,$(1)); \
	mkdir -p swt/$(call full-platform,$(1)); \
	unzip -o -d swt/$(call full-platform,$(1)) $(call swt-zip,$(1)); \
	curl -Of $(web-host)/proguard$(proguard-version).tar.gz; \
	tar xzf proguard$(proguard-version).tar.gz; \
	curl -Of $(web-host)/lzma$(lzma-version).tar.bz2; \
	(mkdir -p lzma-$(lzma-version) \
		&& cd lzma-$(lzma-version) \
		&& tar xjf ../lzma$(lzma-version).tar.bz2); \
	curl -Of $(web-host)/avian-$(version).tar.bz2; \
	tar xjf avian-$(version).tar.bz2; \
	curl -Of $(web-host)/avian-swt-examples-$(version).tar.bz2; \
	tar xjf avian-swt-examples-$(version).tar.bz2; \
	$(call git-clone,$(1)) \
	cd avian-swt-examples; \
	make lzma=$$(pwd)/../lzma-$(lzma-version) \
		full-platform=$(call full-platform,$(1)) $(programs);

$(examples):
	@mkdir -p $(build)/swt-examples
	@echo "making examples for $(call full-platform,$(@)) on $(call build-user,$(@))@$(call build-host,$(@)):$(call build-port,$(@))"
	ssh -p $(call build-port,$(@)) $(call build-user,$(@))@$(call build-host,$(@)) \
		'$(call build-sequence,$(@))'
	set -e; for x in $(programs); do \
		$(rsync) --rsh="ssh -p $(call build-port,$(@))" $(call build-user,$(@))@$(call build-host,$(@)):$(call tmpdir,$(@))/avian-swt-examples/build/$(call full-platform,$(@))-lzma/$${x}/$${x}$(call extension,$(@)) $(build)/swt-examples/$(call full-platform,$(@))/; \
	done
	@mkdir -p $(dir $(@))
	@touch $(@)

test-sequence = \
	set -e; \
	rm -rf $(call tmpdir,$(1)); \
	mkdir -p $(call tmpdir,$(1)); \
	cd $(call tmpdir,$(1)); \
	curl -Of $(web-host)/avian-$(version).tar.bz2; \
	tar xjf avian-$(version).tar.bz2; \
	$(call git-clone,$(1)) \
	cd avian; \
	make arch=$(call arch,$(1)) platform=$(call platform,$(1)) \
		remote-test-user=$(call test-user,$(1)) \
		remote-test-host=$(call test-host,$(1)) \
		remote-test-port=$(call test-port,$(1)) test; \
	cd && rm -rf $(call tmpdir,$(1));

$(tests):
	@echo "building $(call full-platform,$(@)) on $(call build-user,$(@))@$(call build-host,$(@)):$(call build-port,$(@)) and testing on $(call test-user,$(@))@$(call test-host,$(@)):$(call test-port,$(@))"
	ssh -p $(call build-port,$(@)) $(call build-user,$(@))@$(call build-host,$(@)) '$(call test-sequence,$(@))'
	@mkdir -p $(dir $(@))
	@touch $(@)

ci-sequence = \
	set -e; \
	if [ -z "$$JAVA_HOME" ]; then export JAVA_HOME=/cygdrive/e/jdk7; fi; \
	rm -rf $(call tmpdir,$(1)); \
	mkdir -p $(call tmpdir,$(1)); \
	cd $(call tmpdir,$(1)); \
	curl -Of $(web-host)/avian-$(version).tar.bz2; \
	tar xjf avian-$(version).tar.bz2; \
	$(call git-clone,$(1)) \
	cd avian; \
	skip_jdk_test=true bash test/ci.sh \
		arch=$(call arch,$(1)) platform=$(call platform,$(1)); \
	cd && rm -rf $(call tmpdir,$(1));

$(ci-tests):
	@echo "running ci.sh for $(call full-platform,$(@)) on $(call test-user,$(@))@$(call test-host,$(@)):$(call test-port,$(@))"
	ssh -p $(call test-port,$(@)) $(call test-user,$(@))@$(call test-host,$(@)) '$(call ci-sequence,$(@))'
	@mkdir -p $(dir $(@))
	@touch $(@)

all-sources = \
	_config.yml \
	index.html \
	$(shell find _includes _layouts _plugins images javascripts stylesheets \
		-name *.html -or \
		-name *.md -or \
		-name *.rb -or \
		-name *.png -or \
		-name *.css)

$(out)/index.html: $(all-sources)
	jekyll build --trace

.PHONY: clean
clean:
	@echo "removing $(build)"
	rm -rf $(build)

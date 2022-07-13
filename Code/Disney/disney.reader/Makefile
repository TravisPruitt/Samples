#
# Makefile - make the grover-console-image
# - "git pull" all of the appropriate directories
# bitbake the files to produce the image.
#

# main directories used here
BRANCH  := synapse

SYNAPSE := arago
OE      := arago-oe-dev
BITBAKE := arago-bitbake
DLOADS  := arago-downloads
KERNEL  := kernel
GIT_REPO_DIRS := $(OE) $(BITBAKE) $(DLOADS)

# configuration files; copy CFGSRC -> CFGLOC, then customize CFGLOC.
CFGLOC  := $(SYNAPSE)/conf/local.conf
CFGSRC  := $(SYNAPSE)/conf/local.conf.synapse

BUILD   := arago-tmp
DEPLOY  := arago-tmp/deploy/glibc/ipk
GITOLITE:= gitolite@git.synapse.com

OEBASE:=$(PWD)
BBPATH:=$(OEBASE)/$(SYNAPSE):$(OEBASE)/$(OE)
EXPATH:=/opt/arm-2009q1/bin/:$(OEBASE)/$(BITBAKE)/bin:$(OEBASE)/arago-utils/:$(PATH)
BB_ENV_EXTRAWHITE=OEBASE MACHINE META_SDK_PATH TOOLCHAIN_TYPE TOOLCHAIN_BRAND http_proxy https_proxy ftp_proxy no_proxy GIT_PROXY_COMMAND

MACHINE ?= overo

export OEBASE
export BBPATH
export PATH:=$(EXPATH)
export BB_ENV_EXTRAWHITE

$(shell if [ ! -f .git/hooks/post-merge ]; then cp tools/git-hooks/* .git/hooks/; fi)

.PHONY: all
all:
	@echo "make [target]"
	@echo "available targets:"
	@echo "  <application>-image   : corresponding image"
	@echo "                          dap-reader-image, grover-image (and others)"
	@echo "  <image> card          : make SD card for a (above) image"
	@echo "  <application>         : force compile and make corresponding .ipk file"
	@echo "                          dap-reader, grover, and others"
	@echo "  <application>-clean   : clean corresponding application"
	@echo "  <application>-release : Build release opkg repository for corresponding"
	@echo "                          application. grover & dap-reader."
	@echo "  clean                 : remove image from distribution directory"
	@echo " DANGER! ITEMS BELOW MEAN COMPLETE RECOMPILE (45 min)"
	@echo "  clean_build           : Remove all work/compiled files"
	@echo "  clean_all             : clean_build AND remove all of the repos"

# IMAGE TARGETS
#----------------------------------------
IMAGES   := \
grover-image \
dap-reader-image \
xtp-reader-image \
mayhem-development-image \
test-server-image

IMAGES_EXTRA := $(IMAGES) \
xbr4-image

# IPKG TARGETS
#----------------------------------------
# capable of having a release-repository created for them.
RELEASEABLE := \
dap-reader \
xtp-reader \
grover

# create the ipkg distribution file for the specified application
PCKGS := $(RELEASEABLE) \
arping \
radio-cli \
test-server \
vcom \
radio-driver \
setinterfaces \
setmac \
sshkey

#----------------------------------------

# I = image to create; C = card, if defined
I := $(firstword $(MAKECMDGOALS))

PKGVERSION := $(shell OEBASE=$(OEBASE) tools/get_version.sh $(I))

# files that comprise the SD-package
IMG     	:= $(BUILD)/deploy/glibc/images/$(MACHINE)

IMLO		:= $(IMG)/MLO-$(MACHINE)

ifeq ($(MACHINE), xbr4)
IUBOOT		:= $(IMG)/u-boot-$(MACHINE).img
IUBOOTSD	:= u-boot.img
else
IUBOOT		:= $(IMG)/u-boot-$(MACHINE).bin
IUBOOTSD	:= u-boot.bin
endif

IFILESYS	:= $(IMG)/$(I)-$(MACHINE).tar.bz2
IUIMAGE		:= $(IMG)/uImage-$(MACHINE).bin

# UBIFS image for the main NAND partition
UBIFSIMG	:= $(IMG)/$(I)-$(MACHINE).ubi

# Boot script for flashing nand from SD-card
BOOT_SCR=tools/boot_script/boot.scr

# define the newly created directory that contains the SD-package
# but only if the first target is really an image
DIST := $(if $(findstring $(I),$(IMAGES_EXTRA)),$(OEBASE)/images/$(I)/$(PKGVERSION),)

# MACRO-FUNCTIONS
#----------------------------------------
define makePhony =
.PHONY: $(1)
endef

# IMAGE TARGETS
#----------------------------------------

#create .PHONY directives for each image
$(foreach image,$(IMAGES),$(eval $(call makePhony, $(image))))

$(DIST): $(CFGLOC) setenv downloads

$(IMAGES): $(CFGLOC) setenv downloads

$(IMAGES):
	@bitbake $(I)
	@$(MAKE) --no-print-directory $(BOOT_SCR)
	@echo "COPY SD-FILES to $(DIST)"
	@rm -rf $(DIST)
	@mkdir -p $(DIST)
	@cp $(IFILESYS) $(DIST)/filesys-image.tar.bz2
	@cp $(IUBOOT) $(DIST)/$(IUBOOTSD)
	@cp $(IUIMAGE) $(DIST)/uImage
	@cp $(IMLO) $(DIST)/MLO
	@cp $(UBIFSIMG) $(DIST)/rootfs.ubi
	@cp $(BOOT_SCR) $(DIST)

.PHONY: xbr4-image
xbr4-image:
	@MACHINE=xbr4 $(MAKE) grover-image
	@mkdir -p images/xbr4-image
	@echo "MOVE SD-FILES to $(DIST)"
	@rm -fr $(DIST)
	@mv $(OEBASE)/images/grover-image/$(PKGVERSION) $(DIST)
	@cp $(OEBASE)/tools/boot_script/uEnv.txt $(DIST)
	@rm -f $(DIST)/boot.scr

# this builds the recipe from scratch, copies the .ipk file from
# the deploy directory into the images directory, changing it's filename
# to reflect the git-describe-version.

RECIPE = arago-custom/recipes/synapse-$(I)/synapse-$(I).bb

#create .PHONY directives for each package
$(foreach prog,$(PCKGS),$(eval $(call makePhony, $(prog))))

# recipe to re-compile
$(PCKGS): $(CFGLOC) setenv $(RECIPE)
	@echo "BITBAKE $@"
	@./setenv && bitbake -b $(RECIPE) -c compile -f
	@./setenv && bitbake -b $(RECIPE)
	@mkdir -p $(OEBASE)/images
	@if [ ! -d deploy ]; then ln -s $(DEPLOY) deploy; fi
	@cp $$(ls -t `find ./deploy/ -name synapse-$(I)_*` | grep -m1 ".*") $(OEBASE)/images
	@echo "Package = $$(ls -t `find ./images/ -name synapse-$(I)_*` | grep -m1 '.*')"

# Define cleaning versions of the package targets
CLEAN_PCKGS = $(PCKGS:%=%-clean)

$(foreach prog,$(CLEAN_PCKGS),$(eval $(call makePhony, $(prog))))

$(CLEAN_PCKGS): $(CFGLOC) setenv
	@echo "$@"
	@./setenv && bitbake -b $(subst -clean,,$(RECIPE)) -c clean

.PHONY: xbr4-clean
xbr4-clean:
	@ MACHINE=xbr4 $(MAKE) grover-clean

# Define devshell versions of the package targets
DSHLL_PCKGS = $(PCKGS:%=%-devshell)

$(foreach prog,$(DSHLL_PCKGS),$(eval $(call makePhony, $(prog))))

$(DSHLL_PCKGS): $(CFGLOC) setenv $(RECIPE)
	@echo "$@"
	@./setenv && bitbake -b $(subst -devshell,,$(RECIPE)) -c devshell

# RELEASE REPOSITORY (opkg) TARGETS
#----------------------------------------

# These targets build their corresponding images (i.e. dap-rader or grover),
# extract their packages and select dependancies into a release directory,
# and then generate a new opkg index in that directory.

RELEASE_PKGS := $(patsubst %,%-release,$(RELEASEABLE))
RBNAME = $(subst -release,,$(I))
$(foreach prog,$(RELEASE_PKGS),$(eval $(call makePhony, $(prog))))

$(RELEASE_PKGS):
	@echo "$@"
	@$(MAKE) $(RBNAME)-image
	@tools/build-release-repo.sh \
		synapse-$(RBNAME) \
		$(OEBASE)/release/$(RBNAME)-$(PKGVERSION)

.PHONY: xbr4-release
xbr4-release:
	@MACHINE=xbr4 $(MAKE) grover-release
	@echo "MOVE SD-FILES to $(OEBASE)/images/xbr4-image/$(PKGVERSION)"
	@rm -fr $(OEBASE)/images/xbr4-image/$(PKGVERSION)
	@mv $(OEBASE)/images/grover-image/$(PKGVERSION) $(OEBASE)/images/xbr4-image/$(PKGVERSION)
	@cp $(OEBASE)/tools/boot_script/uEnv.txt $(OEBASE)/images/xbr4-image/$(PKGVERSION)
	@rm -f $(OEBASE)/images/xbr4-image/$(PKGVERSION)/boot.scr
	@echo "MOVE release files to $(OEBASE)/release/xbr4-$(PKGVERSION)"
	@rm -fr $(OEBASE)/release/xbr4-$(PKGVERSION)
	@mv $(OEBASE)/release/grover-$(PKGVERSION) $(OEBASE)/release/xbr4-$(PKGVERSION)

.PHONY: base-release
base-release:
	@echo "$@"
	@$(foreach pkg,$(RELEASE_PKGS),$(MAKE) $(pkg) && ) true
	@tools/build-base-repo.sh $(OEBASE)/release/$@

# REPOSITORY RULES
#----------------------------------------

.PHONY: pull
pull: $(GIT_REPO_DIRS)
	@$(foreach gitrepodir, $?, \
		echo "PULL $(gitrepodir)" \
		&& pushd $(gitrepodir)>/dev/null \
		&& git checkout $(BRANCH) \
		&& git pull \
		&& popd>/dev/null &&) true
	@git submodule init
	@git submodule update
	@$(foreach gitrepodir, $?, \
		pushd $(gitrepodir)>/dev/null \
		&& printf "%-16.16s = " "$(gitrepodir)" \
		&& git describe \
		&& popd>/dev/null && ) true


.PHONY: show
show: $(GIT_REPO_DIRS) $(KERNEL)
	@echo "SHOW git"
	@$(foreach gitrepodir, $?, \
		pushd $(gitrepodir)>/dev/null \
		&& printf "%-16.16s = " "$(gitrepodir)" \
		&& git describe \
		&& popd>/dev/null && ) true


# SECONDARY TARGETS
#----------------------------------------

$(BOOT_SCR):
	@$(MAKE) -C $(@D)

oe: Makefile | $(OE)
	@echo "PULL git:$@ "
	@cd $(OE) && git checkout $(BRANCH) && git pull
	@echo "(DONE)"

bitbake: Makefile | $(BITBAKE)
	@echo "PULL git:$? "
	@cd $(BITBAKE) && git checkout $(BRANCH) && git pull
	@echo "(DONE)"

dloads: Makefile | $(DLOADS)
	@echo "PULL git:$@ "
	@cd $(DLOADS) && git checkout $(BRANCH) && git pull
	@echo "(DONE)"

$(GIT_REPO_DIRS) :
	@echo "CLONE git:$@ "
	@git clone -b $(BRANCH) $(GITOLITE):disney.$@.git $@;
	@echo "(DONE)"

downloads: Makefile | $(DLOADS)
	ln -sf $(DLOADS) $@
	@echo "(DONE)"

setenv: Makefile | $(SYNAPSE)
	@echo "CREATE $@"
	@echo "export OEBASE=$(OEBASE)" > setenv
	@echo "export BBPATH=$(BBPATH)" >> setenv
	@echo "export PATH=/usr/lib/python2.7:$(PATH)" >> setenv
	@echo "export BB_ENV_EXTRAWHITE=\"$(BB_ENV_EXTRAWHITE)\"" >> setenv
	@chmod +x $@
	@echo "(DONE)"

# if SYNAPSE exists, then CFGSRC will also.
$(CFGSRC): | $(GIT_REPO_DIRS)

$(CFGLOC): $(CFGSRC)
	@echo "CREATE & CUSTOMIZE $@"
	cp -n $? $@

# ALTERNATE TARGETS
#----------------------------------------

.PHONY: clean
clean:
	@echo "CLEAN"
	@rm -rf $(DIST)
	@echo "(DONE)"

.PHONY: clean_all
clean_all: clean_build clean_repos

.PHONY: clean_build
clean_build:
	@echo "CLEAN BUILD / WORK FILES"
	@rm -rf $(BUILD)
	@rm -f $(BOOT_SCR)
	@echo "(DONE)"

.PHONY: clean_repos
clean_repos:
	@echo "CLEAN REPOS"
	@rm -rf $(GIT_REPO_DIRS)
	@echo "(DONE)"

# Build date, self explanatory
BUILDDATE=$(shell date "+%Y %b %d-%R:%S")
# Git hash for hard ID
GIT_HASH:=$(shell git rev-parse HEAD)
# Number of commits from the start of this tree (kinda like build number)
COMMITS:=$(shell git rev-list 9133022.. --count)
# Name of the branch
BRANCH:=$(shell git describe --contains --all HEAD)
ifeq ($(BRANCH), xbr-release)
  BUILD_TYPE=
else
  BUILD_TYPE=- $(BRANCH)
endif
FULL_VERSION=$(VERSION)-$(COMMITS) $(BUILDDATE) $(BUILD_TYPE) ($(GIT_HASH))

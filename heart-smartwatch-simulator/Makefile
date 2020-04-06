default: help

PROJECTNAME=$(shell basename "$(PWD)")

BIN_FOLDER=bin
BIN_FOLDER_MACOS=${BIN_FOLDER}/amd64/darwin
BIN_FOLDER_WINDOWS=${BIN_FOLDER}/amd64/windows
BIN_FOLDER_LINUX=${BIN_FOLDER}/amd64/linux
BIN_FOLDER_SCRATCH=${BIN_FOLDER}/amd64/scratch
BIN_NAME=${PROJECTNAME}

# Make is verbose in Linux. Make it silent.
MAKEFLAGS += --silent
LDFLAGS=-X main.buildDate=`date -u +%Y%m%d-%H%M%S` -X main.version=`scripts/version.sh`

## compile: compiles project in current system
compile: clean get generate fmt vet test build

## release: generate binaries and an archive containing all binaries in bin/ folder
release: clean get generate fmt vet test build-all archive-all

## watch: format, test and build project at go files modification
watch:
	@echo "  >  Watching go files..."
	@if type "ag" > /dev/null 2>&1; then if type "entr" > /dev/null 2>&1; then ag -l | entr make clean generate fmt vet test-colorized build; else echo "Please install entr: http://eradman.com/entrproject/"; fi else echo "Please install silver searcher: https://github.com/ggreer/the_silver_searcher"; fi

# ---------------------------------------------------------------------------

archive-all: archive-macos archive-windows archive-linux archive-alpine-scratch

archive-macos:
	@echo "  >  Generating archive for MacOS"
	@-tar czvf ${BIN_FOLDER}/${BIN_NAME}-amd64-darwin.tar.gz -C ${BIN_FOLDER_MACOS} ${BIN_NAME}

archive-windows:
	@echo "  >  Generating archive for Windows"
	@-zip ${BIN_FOLDER}/${BIN_NAME}-amd64-windows.zip -j ${BIN_FOLDER_WINDOWS}/${BIN_NAME}.exe

archive-linux:
	@echo "  >  Generating archive for Linux"
	@-tar czvf ${BIN_FOLDER}/${BIN_NAME}-amd64-linux.tar.gz -C ${BIN_FOLDER_LINUX} ${BIN_NAME}

archive-alpine-scratch:
	@echo "  >  Generating archive for Alpine/Scratch"
	@-tar czvf ${BIN_FOLDER}/${BIN_NAME}-amd64-scratch.tar.gz -C ${BIN_FOLDER_SCRATCH} ${BIN_NAME}

clean:
	@echo "  >  Cleaning build cache"
	@-rm -rf ${BIN_FOLDER} && go clean

build:
	@echo "  >  Building binary"
	@go build -ldflags="${LDFLAGS}" -o ${BIN_FOLDER}/${BIN_NAME}

build-all: build-macos build-windows build-linux build-alpine-scratch

build-macos:
	@echo "  >  Building binary for MacOS"
	@GOOS=darwin GOARCH=amd64 go build -ldflags="${LDFLAGS}" -o ${BIN_FOLDER_MACOS}/${BIN_NAME}

build-windows:
	@echo "  >  Building binary for Windows"
	@GOOS=windows GOARCH=amd64 go build -ldflags="${LDFLAGS}" -o ${BIN_FOLDER_WINDOWS}/${BIN_NAME}.exe

build-linux:
	@echo "  >  Building binary for Linux"
	@GOOS=linux GOARCH=amd64 go build -ldflags="${LDFLAGS}" -o ${BIN_FOLDER_LINUX}/${BIN_NAME}

# Alpine & scratch base images use musl instead of gnu libc, thus we need to add additional parameters on the build
build-alpine-scratch:
	@echo "  >  Building binary for Alpine/Scratch"
	@CGO_ENABLED=0 GOOS=linux GOARCH=amd64 go build -ldflags="${LDFLAGS}" -a -installsuffix cgo -o ${BIN_FOLDER_SCRATCH}/${BIN_NAME}

fmt:
	@echo "  >  Formatting code"
	@go fmt ./...

generate:
	@echo "  >  Go generate"
	@if type "stringer" > /dev/null 2>&1; then go generate ./...; else GO111MODULE=off go get golang.org/x/tools/cmd/stringer && go generate ./...; fi

get:
	@echo "  >  Checking if there is any missing dependencies..."
	@go get

test:
	@echo "  >  Executing unit tests"
	@go test -v -timeout 60s -race ./...

test-colorized:
	@echo "  >  Executing unit tests"
	@if type "richgo" > /dev/null 2>&1; then richgo test -v -timeout 60s -race ./...; else GO111MODULE=off go get github.com/kyoh86/richgo && richgo test -v -timeout 60s -race ./...; fi

vet:
	@echo "  >  Checking code with vet"
	@go vet ./...

.PHONY: help
all: help
help: Makefile
	@echo
	@echo " Choose a command run in "$(PROJECTNAME)":"
	@echo
	@sed -n 's/^##//p' $< | column -t -s ':' |  sed -e 's/^/ /'
	@echo

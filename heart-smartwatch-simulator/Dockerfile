FROM golang:1.14 AS builder

WORKDIR /opt/app

COPY go.mod go.sum ./
RUN go mod download

COPY . .
RUN make get clean build-alpine-scratch
# --------
FROM scratch

WORKDIR /

COPY --from=builder /opt/app/bin/amd64/scratch .

ENTRYPOINT [ "/app" ]
CMD ["--help"]

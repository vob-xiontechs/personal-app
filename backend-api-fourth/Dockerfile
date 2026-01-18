# Build stage
FROM golang:1.21-alpine AS builder

WORKDIR /app

COPY go.mod main.go ./

RUN go mod tidy

RUN go mod download

COPY . .

RUN go mod tidy

RUN go build -o main .

# Run stage
FROM alpine:latest

RUN apk --no-cache add ca-certificates

WORKDIR /root/

COPY --from=builder /app/main .

EXPOSE 8080

CMD ["./main"]

variable "region" {
  type    = string
  default = "us-east-1"
}

variable "instance_type" {
  type    = string
  default = "t2.micro" # free tier
}

variable "my_ip_cidr" {
  default = "0.0.0.0/0"
  type        = string
}

variable "app_jar_url" {
  default = "https://mi-bucket-bootcamp.s3.us-east-1.amazonaws.com/bootcamp-api-0.0.1-SNAPSHOT.jar?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIA4PIFL6EEGRJ63BTJ%2F20250925%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250925T202209Z&X-Amz-Expires=3600&X-Amz-SignedHeaders=host&X-Amz-Signature=879996a5d85e2fabfe0e935e017e31477ff8c96a5206a7d5d9dabb660fe13654"
  type        = string
}

variable "app_port" {
  type    = number
  default = 8080
}

variable "app_name" {
  type    = string
  default = "bootcamp-api"
}

variable "dynamodb_table_arn" {
  type        = string
  default     = ""
  description = ""
}

output "public_ip" {
  value = aws_instance.app.public_ip
}

output "public_dns" {
  value = aws_instance.app.public_dns
}

output "app_url" {
  value = "http://${aws_instance.app.public_ip}:${var.app_port}/actuator/health"
}

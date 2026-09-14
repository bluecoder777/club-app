#!/bin/bash
echo "Stopping all containers..."
docker compose down -v
echo "Removing dangling images..."
docker image prune -f
echo "Done! Run 'docker compose up --build -d' to start fresh."

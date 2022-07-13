# Mayhem console image
# gives you an image with basic media libraries, but not application pre-installed

require mayhem-image.inc

IMAGE_INSTALL += "\
    task-mayhem-base \
    task-mayhem-console \
    "

export IMAGE_BASENAME = "mayhem-image"

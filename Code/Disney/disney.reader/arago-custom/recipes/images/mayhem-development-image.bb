# Mayhem development image

require mayhem-image.inc

IMAGE_INSTALL += "\
    task-mayhem-base \
    task-mayhem-console \
    task-mayhem-development \
    "

export IMAGE_BASENAME = "mayhem-development-image"

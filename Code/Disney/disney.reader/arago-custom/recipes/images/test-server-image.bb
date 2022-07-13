# Grover console image
# gives you an image with basic media libraries and with the long range reader
# application (grover) pre-installed and set up to run automatically

require mayhem-image.inc


# task-mayhem-development: 
# leave this in during development. This can be removed as we are ready for 
# production release, then another image.bb would be created for development...

IMAGE_INSTALL += "\
    task-mayhem-base \
    task-mayhem-console \
    task-mayhem-development \
    dhcp-server \
    synapse-setinterfaces \
    synapse-test-server \
    "

export IMAGE_BASENAME = "test-server-image"

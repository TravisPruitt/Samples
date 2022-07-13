# Dap reader console image
# gives you an image with basic media libraries and with the short range reader
# application (gonzo) pre-installed and set up to run automatically


require mayhem-image.inc

IMAGE_INSTALL += "\
    task-mayhem-base \
    task-mayhem-console \
    task-mayhem-audio \
    synapse-vcom \
    omap3-pwm \
    dhcp-client \
    synapse-sslcert \
    synapse-dap-reader \
    "

export IMAGE_BASENAME = "dap-reader-image"

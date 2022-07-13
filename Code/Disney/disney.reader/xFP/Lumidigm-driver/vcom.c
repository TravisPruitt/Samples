/*
 * Lumidigm Venus Driver
 *
 *
 */

//#include <linux/config.h>
#include <linux/kernel.h>
#include <linux/errno.h>
#include <linux/init.h>
#include <linux/slab.h>
#include <linux/module.h>
#include <linux/kref.h>
#include <linux/usb.h>
#include <asm/uaccess.h>


//#define LUMI_DBG 1

/* Define these values to match your devices */
#define USB_VENUS_VENDOR_ID	0x0525
#define USB_VENUS_PRODUCT_ID	0x3424
#define USB_MERCURY_VENDOR_ID	0x1FAE
#define USB_MERCURY_PRODUCT_ID	0x212C

/* table of devices that work with this driver */
static struct usb_device_id id_table [] = {
	{ USB_DEVICE(USB_VENUS_VENDOR_ID, USB_VENUS_PRODUCT_ID) },
	{ USB_DEVICE(USB_MERCURY_VENDOR_ID, USB_MERCURY_PRODUCT_ID) },
	{ }					/* Terminating entry */
};
MODULE_DEVICE_TABLE (usb, id_table);
/* from the official device number list Sep 9, 2007 

232 char	Biometric Devices
		0 = /dev/biometric/sensor0/fingerprint	first fingerprint sensor on first device
		1 = /dev/biometric/sensor0/iris		first iris sensor on first device
		2 = /dev/biometric/sensor0/retina	first retina sensor on first device
		3 = /dev/biometric/sensor0/voiceprint	first voiceprint sensor on first device
		4 = /dev/biometric/sensor0/facial	first facial sensor on first device
		5 = /dev/biometric/sensor0/hand		first hand sensor on first device
		  ...
		10 = /dev/biometric/sensor1/fingerprint	first fingerprint sensor on second device
		  ...
		20 = /dev/biometric/sensor2/fingerprint	first fingerprint sensor on third device
		  ...
*/

/* Get a minor range for your devices from the usb maintainer */
#define USB_VCOM_MINOR_BASE 	192

/* Structure to hold all of our device specific stuff */
struct usb_vcom {
	struct usb_device *	udev;			/* the usb device for this device */
	struct usb_interface *	interface;		/* the interface for this device */
	unsigned char *		bulk_in_buffer;		/* the buffer to receive data */
	size_t			bulk_in_size;		/* the size of the receive buffer */
	__u8			bulk_in_endpointAddr;	/* the address of the bulk in endpoint */
	__u8			bulk_out_endpointAddr;	/* the address of the bulk out endpoint */
	struct kref		kref;
};
#define to_vcom_dev(d) container_of(d, struct usb_vcom, kref)

static struct usb_driver vcom_driver;

static void vcom_delete(struct kref *kref)
{	
	struct usb_vcom *dev = to_vcom_dev(kref);

	usb_put_dev(dev->udev);
	kfree (dev->bulk_in_buffer);
	kfree (dev);
}

static int vcom_open(struct inode *inode, struct file *file)
{
	struct usb_vcom *dev;
	struct usb_interface *interface;
	int subminor;
	int retval = 0;
	subminor = iminor(inode);

#ifdef LUMI_DBG
	printk("vcom_open: --- START ---\n");
#endif

	interface = usb_find_interface(&vcom_driver, subminor);
	if (!interface) 
        {
		retval = -ENODEV;
	        printk("vcom_open: Could not find interface\n");
		goto exit;
	}

	dev = usb_get_intfdata(interface);
	if (!dev) 
        {
		retval = -ENODEV;
	        printk("vcom_open: Could not get interface data\n");
		goto exit;
	}
	
	/* increment our usage count for the device */
	kref_get(&dev->kref);

	/* save our object in the file's private structure */
	file->private_data = dev;

exit:
	return retval;
}

static int vcom_release(struct inode *inode, struct file *file)
{
	struct usb_vcom *dev;
	dev = (struct usb_vcom *)file->private_data;
	if (dev == NULL)
		return -ENODEV;

	/* decrement the count on our device */
	kref_put(&dev->kref, vcom_delete);
	return 0;
}

static ssize_t vcom_read(struct file *file, char __user *buffer, size_t count, loff_t *ppos)
{
	struct usb_vcom *dev;
	int retval = 0;
	int readOK = 0;
	dev = (struct usb_vcom *)file->private_data;

	/* do a blocking bulk read to get data from the device */
#ifdef LUMI_DBG
	printk("vcom_read: --- START ---\n");
	printk("vcom_read: Reading %zu bytes of data from the bulk endpoint. (Blocking...)\n", min(dev->bulk_in_size, count));
#endif
	retval = usb_bulk_msg(dev->udev,
			      usb_rcvbulkpipe(dev->udev, dev->bulk_in_endpointAddr),
			      dev->bulk_in_buffer,
			      min(dev->bulk_in_size, count),
			      &readOK,
			      HZ*10);

	/* if the read was successful, copy the data to userspace */
	if (0 == retval)
        {
#ifdef LUMI_DBG
		printk("vcom_read: Read in %d bytes of data from the bulk endpoint.\n", readOK);
		printk("vcom_read: Copy data to user space buffer...\n");
#endif
		if (copy_to_user(buffer, dev->bulk_in_buffer, count))
		{
			printk("vcom_read: Copy failed!\n");
			retval = -EFAULT;
		}
		else
			retval = count;

		////printk(KERN_ALERT "Read these bytes:\n");
		//printk("vcom_read: Read these bytes:\n");
		//for(i=0;i<count;i++)
		//{
		//	printk("vcom_read: 0x%02X:\n",dev->bulk_in_buffer[i]);
		//	//printk(KERN_ALERT ":0x%02X:",dev->bulk_in_buffer[i]);
		//}
	}
	else
	{	
		if(retval == -ETIMEDOUT)
		{
			//If timedout, just return 0 bytes read 
			printk("vcom_read: Read failed! Read timed out. Returning 0 so it can be handled by transport.\n");
			retval = 0;
		}
		else
			printk("vcom_read: Read failed! Returned %d\n", retval);
	}

#ifdef LUMI_DBG
	printk("vcom_read: --- EXIT ---\n");
#endif
	return retval;
}

static ssize_t vcom_write(struct file *file, const char __user *user_buffer, size_t count, loff_t *ppos)
{
	struct usb_vcom *dev;
	int retval = 0;
	int writeOK = 0;
	char *buf = NULL;
	dev = (struct usb_vcom *)file->private_data;
	
#ifdef LUMI_DBG
	printk("vcom_write: --- START ---\n");
#endif

	/* verify that we actually have some data to write */
	if (count == 0)
	{
		printk("vcom_write: No data sent, count was 0\n");
		goto exit;
	}
	
	if(user_buffer == NULL)
	{
		printk("vcom_write: No data sent, user_buffer was NULL\n");
		goto exit;
	}
	
	// Allocate Kernel Memory, and Copy data to it
#ifdef LUMI_DBG
	printk("vcom_write: Function received %zu bytes of size %zu\n", count, sizeof(*user_buffer));
	printk("vcom_write: Allocating %zu byte kernel space buffer...\n", count);
#endif
	buf = kmalloc(count, GFP_KERNEL);
	if (!buf) 
	{
		printk("vcom_write: Memory allocation failed!\n");
		retval = -ENOMEM;
		goto error;
	}

#ifdef LUMI_DBG
	printk("vcom_write: Copy data to kernel space buffer...\n");
#endif
	if (copy_from_user(buf, user_buffer, count)) 
        {
		printk("vcom_write: Copy failed!\n");
		retval = -EFAULT;
		goto error;
	}

#ifdef LUMI_DBG
	// Write the buffer to the device
	printk("vcom_write: Write data to the bulk endpoint. (Blocking...)\n");
#endif
	retval = usb_bulk_msg(dev->udev,
              		      usb_sndbulkpipe(dev->udev, dev->bulk_out_endpointAddr),
               		      buf,
               		      count,
               		      &writeOK, 
			      HZ*10);

	// Check if the write was successful 
	if (0 != retval)
	{
		if(retval == -ETIMEDOUT)
		{
			//If timedout, just return 0 bytes read 
			printk("vcom_write: Write failed! Write timed out. Returning 0 so it can be handled by transport.\n");
			count = 0;
		}
		else
		{
			printk("vcom_write: Write failed returned %d\n", retval);
			goto error;
		}
	}

#ifdef LUMI_DBG
	printk("vcom_write: Wrote %d bytes to the bulk endpoint.\n", writeOK);
#endif
	// Free memory allocated in kernel space
	kfree(buf);

exit:
#ifdef LUMI_DBG
	printk("vcom_write: --- EXIT ---\n");
#endif
	return count;

error:
	kfree(buf);
	printk("vcom_write: --- EXIT --- With Errors!\n");
	return retval;
}

static struct file_operations vcom_fops = {
	.owner =	THIS_MODULE,
	.read =		vcom_read,
	.write =	vcom_write,
	.open =		vcom_open,
	.release =	vcom_release,
};

/* 
 * usb class driver info in order to get a minor number from the usb core,
 * and to have the device registered with devfs and the driver core
 */
static struct usb_class_driver vcom_class = {
	.name = "usb/vcom%d",
	.fops = &vcom_fops,
//	.mode = S_IFCHR | S_IRUSR | S_IWUSR | S_IRGRP | S_IWGRP | S_IROTH,
	.minor_base = USB_VCOM_MINOR_BASE,
	//	.mode = S_IFCHR | S_IRUSR | S_IWUSR | S_IRGRP | S_IWGRP | S_IROTH,
	.minor_base = USB_VCOM_MINOR_BASE,
};

static int vcom_probe(struct usb_interface *interface, const struct usb_device_id *id)
{
	struct usb_vcom *dev = NULL;
	struct usb_host_interface *iface_desc;
	struct usb_endpoint_descriptor *endpoint;
	size_t buffer_size;
	int i;
	int retval = -ENOMEM;
	//dev_t first;


	/* allocate memory for our device state and initialize it */
	dev = kmalloc(sizeof(struct usb_vcom), GFP_KERNEL);
	if (dev == NULL) 
        {
		err("vcom_probe: Out of memory");
		printk("vcom_probe: Out of memory\n");
		goto error;
	}
	memset(dev, 0x00, sizeof (*dev));
	kref_init(&dev->kref);

	dev->udev = usb_get_dev(interface_to_usbdev(interface));
	dev->interface = interface;

	/* set up the endpoint information */
	/* use only the first bulk-in and bulk-out endpoints */
	iface_desc = interface->cur_altsetting;
	for (i = 0; i < iface_desc->desc.bNumEndpoints; ++i) 
        {
		endpoint = &iface_desc->endpoint[i].desc;

		if (!dev->bulk_in_endpointAddr &&
		    (endpoint->bEndpointAddress & USB_DIR_IN) &&
		    ((endpoint->bmAttributes & USB_ENDPOINT_XFERTYPE_MASK) == USB_ENDPOINT_XFER_BULK)) 
                {
			buffer_size = 0x10000;
			//buffer_size = endpoint->wMaxPacketSize;

			/* we found a bulk in endpoint */
#ifdef LUMI_DBG
			printk("vcom_probe: wMaxPacketSize: %d setting to %zu\n", endpoint->wMaxPacketSize, buffer_size);
			printk("vcom_probe: wMaxPacketSize: %zu\n", buffer_size);
#endif
			dev->bulk_in_size = buffer_size;
			dev->bulk_in_endpointAddr = endpoint->bEndpointAddress;
			dev->bulk_in_buffer = kmalloc(buffer_size, GFP_KERNEL);
			if (!dev->bulk_in_buffer) 
                        {
				err("vcom_probe: Could not allocate bulk_in_buffer\n");
				printk("vcom_probe: Could not allocate bulk_in_buffer\n");
				goto error;
			}
		}

		if (!dev->bulk_out_endpointAddr &&
		    !(endpoint->bEndpointAddress & USB_DIR_IN) &&
		    ((endpoint->bmAttributes & USB_ENDPOINT_XFERTYPE_MASK) == USB_ENDPOINT_XFER_BULK)) 
                {
			/* we found a bulk out endpoint */
			dev->bulk_out_endpointAddr = endpoint->bEndpointAddress;
		}
	}
	if (!(dev->bulk_in_endpointAddr && dev->bulk_out_endpointAddr)) 
        {
		printk("vcom_probe: Could not find both bulk-in and bulk-out endpoints\n");
		err("vcom_probe: Could not find both bulk-in and bulk-out endpoints\n");
		goto error;
	}

	/* save our data pointer in this interface device */
	usb_set_intfdata(interface, dev);

	/* we can register the device now, as it is ready */
	retval = usb_register_dev(interface, &vcom_class);
	if (retval) 
        {
		/* something prevented us from registering this driver */
		printk("vcom_probe: Not able to get a minor for this device.\n");
		err("vcom_probe: Not able to get a minor for this device.\n");
		usb_set_intfdata(interface, NULL);
		goto error;
	}

	/* let the user know what node this device is now attached to */
	return 0;

error:
	if (dev)
		kref_put(&dev->kref, vcom_delete);
	return retval;
}

static void vcom_disconnect(struct usb_interface *interface)
{
	struct usb_vcom *dev;
	//int minor = interface->minor;

	dev = usb_get_intfdata(interface);
	usb_set_intfdata(interface, NULL);

	/* give back our minor */
	usb_deregister_dev(interface, &vcom_class);

	/* decrement our usage count */
	kref_put(&dev->kref, vcom_delete);
}

static struct usb_driver vcom_driver = {
//	.owner = THIS_MODULE,
	.name = "vcom",
	.id_table = id_table,
	.probe = vcom_probe,
	.disconnect = vcom_disconnect,
};

static int __init usb_vcom_init(void)
{
	int result;

	/* register this driver with the USB subsystem */
	result = usb_register(&vcom_driver);
	if (result)
	{
		printk("usb_register failed. Error number %d\n", result);
		err("usb_register failed. Error number %d\n", result);
	}
	return result;
}

static void __exit usb_vcom_exit(void)
{
	/* deregister this driver with the USB subsystem */
	usb_deregister(&vcom_driver);
}

module_init (usb_vcom_init);
module_exit (usb_vcom_exit);

MODULE_LICENSE("GPL");

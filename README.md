# Steganography
Steganography is the art and science of concealing text or data in plain sight. One way to do so is by using an image to hide sensitive data. Many algorithms exists to accomplish this, but this project uses the least significant bit (LSB) algorithm.

#LSB Algorithm
All images are composed of pixels. Within each pixel, there are three colors that make up that pixel: red, blue, green. Each color ranges from a value of 0 - 255. To hide text within an image, each character is converted to its ASCII equivalent, but in binary. So for example, "A" has an ASCII value of 65, which translated to 8 bit binary is 01000001. To encode this character in an image, the least significant bit of each color in each pixel will be changed to the corresponding bit in "A". To hide "A", we will need three pixels (three pixels x three colors per pixels = nine bits we can change). Suppose the three pixels have following colors:

# Steganography
Steganography is the art and science of concealing text or data in plain sight. One way to do so is by using an image to hide sensitive data. Many algorithms exists to accomplish this, but this project uses the least significant bit (LSB) algorithm.

#LSB Algorithm
All images are composed of pixels. Within each pixel, there are three colors that make up that pixel: red, blue, green. Each color ranges from a value of 0 - 255. To hide text within an image, each character is converted to its ASCII equivalent, but in binary. So for example, "A" has an ASCII value of 65, which translated to 8 bit binary is 01000001. To encode this character in an image, the least significant bit of each color in each pixel will be changed to the corresponding bit in "A". To hide "A", we will need three pixels (three pixels x three colors per pixels = nine bits we can change). Suppose the three pixels have following colors:

R1: 120 = 01111000

G1: 200 = 11001000

B1: 093 = 01011101

R2: 230 = 11100110

G2: 008 = 00001000

B2: 199 = 11000111

R3: 011 = 00001011

G3: 221 = 11010011

B3: 155 = 10011011

After changing the least significant bit of each color of each pixel to the corresponding bit in "A", the new pixels are as follows:

R1: = 01111000

G1: = 1100100<b>1</b>

B1: = 0101110<b>0</b>

R2: = 11100110

G2: = 00001000

B2: = 1100011<b>0</b>

R3: = 0000101<b>0</b>

G3: = 11010011

B3: = 10011011

With bold bits being ones that were modified in the process and B3 not neing used at all. With this algorithm in place, only the least significant bit was changed and the change in color is not noticable to the naked eye. This same approach can be applied to hide an image within another image. Each image is converted to their RGB components and follow the same procedure as hiding text in an image.

#Compilation
Two versions are included in this project: a GUI and a non-GUI application. Sample texts and images along with the results are included in the project. For GUI application, user can pick and choose from their own collection to encode and decode. 

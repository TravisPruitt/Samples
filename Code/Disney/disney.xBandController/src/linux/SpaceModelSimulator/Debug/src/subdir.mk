################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
C_SRCS += \
../src/chirp.c \
../src/init.c \
../src/main.c \
../src/poster.c \
../src/simulate.c 

OBJS += \
./src/chirp.o \
./src/init.o \
./src/main.o \
./src/poster.o \
./src/simulate.o 

C_DEPS += \
./src/chirp.d \
./src/init.d \
./src/main.d \
./src/poster.d \
./src/simulate.d 


# Each subdirectory must supply rules for building sources it contributes
src/%.o: ../src/%.c
	@echo 'Building file: $<'
	@echo 'Invoking: GCC C Compiler'
	gcc -I/usr/include -I/usr/include/libxml2 -I/usr/local/include -I/usr/include/libxml2 -I/usr/local/include/libxml2 -O0 -g3 -Wall -c -fmessage-length=0 -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@:%.o=%.d)" -o"$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '



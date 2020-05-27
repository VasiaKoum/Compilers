@.Simple_vtable = global [0 x i8*] []

declare i8* @calloc(i32, i32)
declare i32 @printf(i8*, ...)
declare void @exit(i32)

@_cint = constant [4 x i8] c"%d\0a\00"
@_cOOB = constant [15 x i8] c"Out of bounds\0a\00"
@_cNSZ = constant [15 x i8] c"Negative size\0a\00"

define void @print_int(i32 %i) {
	%_str = bitcast [4 x i8]* @_cint to i8*
	call i32 (i8*, ...) @printf(i8* %_str, i32 %i)
	ret void
}

define void @throw_oob() {
	%_str = bitcast [15 x i8]* @_cOOB to i8*
	call i32 (i8*, ...) @printf(i8* %_str)
	call void @exit(i32 1)
	ret void
}

define void @throw_nsz() {
	%_str = bitcast [15 x i8]* @_cNSZ to i8*
	call i32 (i8*, ...) @printf(i8* %_str)
	call void @exit(i32 1)
	ret void
}

define i32 @main(){
	%x = alloca i32

	store i32 10, i32* %x
	%_0 = load i32, i32* %x
	%_1 = icmp slt i32 %_0, 2

	br i1 %_1, label %if_0, label %else_0

if_0:
	%_2 = load i32, i32* %x
	%_3 = icmp slt i32 %_2, 2

	br i1 %_3, label %if_1, label %else_1

if_1:

	call void (i32) @print_int(i32 0)
	br label %fi_1

else_1:
	%_4 = load i32, i32* %x
	%_5 = icmp slt i32 %_4, 2

	br i1 %_5, label %if_2, label %else_2

if_2:
	%_6 = load i32, i32* %x
	%_7 = icmp slt i32 %_6, 2

	br i1 %_7, label %if_3, label %else_3

if_3:

	call void (i32) @print_int(i32 0)
	br label %fi_3

else_3:
	%_8 = load i32, i32* %x
	%_9 = icmp slt i32 %_8, 2

	br i1 %_9, label %if_4, label %else_4

if_4:

	call void (i32) @print_int(i32 0)
	br label %fi_4

else_4:

	call void (i32) @print_int(i32 1)

fi_4:
	br label %fi_3

fi_3:
	br label %fi_2
	br label %fi_2

else_2:

	call void (i32) @print_int(i32 1)

fi_2:
	br label %fi_1

fi_1:
	br label %fi_0
	br label %fi_0

else_0:

	call void (i32) @print_int(i32 1)

fi_0:

	ret i32 0
}

@.test1_vtable = global [0 x i8*] []

@.A_vtable = global [1 x i8*] [
	i8* bitcast (i8* (i8*)* @A.returnA to i8*)
]

@.B_vtable = global [2 x i8*] [
	i8* bitcast (i8* (i8*)* @B.returnA to i8*),
	i8* bitcast (i8* (i8*)* @B.returnB to i8*)
]

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
	%b = alloca i8*
	%a = alloca i8*

	%_0 = call i8* @calloc(i32 1, i32 21)
	%_1 = bitcast i8* %_0 to i8***
	%_2 = getelementptr [2 x i8*], [2 x i8*]* @.B_vtable, i32 0, i32 0
	store i8** %_2, i8*** %_1
	store i8* %_0, i8** %a

	%_3 = load i8*, i8** %a
	%_4 = bitcast i8* %_3 to i8***
	%_5 = load i8**, i8*** %_4
	%_6 = getelementptr i8*, i8** %_5, i32 ?
	%_7 = load i8*, i8** %_6
	%_8 = bitcast i8* %_7 to TYPE 
	%_9 = call TYPE %_8(i8* %_3, 
	store i8* %_9, i8** %a

	ret i32 0
}

define i8* @A.returnA(i8* %this) {

	%_0 = call i8* @calloc(i32 1, i32 12)
	%_1 = bitcast i8* %_0 to i8***
	%_2 = getelementptr [1 x i8*], [1 x i8*]* @.A_vtable, i32 0, i32 0
	store i8** %_2, i8*** %_1
	ret i8* %_0
}

define i8* @B.returnA(i8* %this) {

	call void (i32) @print_int(i32 404)

	%_0 = call i8* @calloc(i32 1, i32 12)
	%_1 = bitcast i8* %_0 to i8***
	%_2 = getelementptr [1 x i8*], [1 x i8*]* @.A_vtable, i32 0, i32 0
	store i8** %_2, i8*** %_1
	ret i8* %_0
}

define i8* @B.returnB(i8* %this) {

	%_0 = call i8* @calloc(i32 1, i32 21)
	%_1 = bitcast i8* %_0 to i8***
	%_2 = getelementptr [2 x i8*], [2 x i8*]* @.B_vtable, i32 0, i32 0
	store i8** %_2, i8*** %_1
	ret i8* %_0
}

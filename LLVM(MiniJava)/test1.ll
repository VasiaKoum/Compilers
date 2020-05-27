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
	%_3 = bitcast i8* %_0 to i8***
	%_4 = load i8**, i8*** %_3
	%_5 = getelementptr i8*, i8** %_4, i32 1
	%_6 = load i8*, i8** %_5
	%_7 = bitcast i8* %_6 to i8* (i8*)*
	%_8 = call i8* %_7(i8* %_0)

	%_9 = bitcast i8* %_8 to i8***
	%_10 = load i8**, i8*** %_9
	%_11 = getelementptr i8*, i8** %_10, i32 1
	%_12 = load i8*, i8** %_11
	%_13 = bitcast i8* %_12 to i8* (i8*)*
	%_14 = call i8* %_13(i8* %_8)

	%_15 = bitcast i8* %_14 to i8***
	%_16 = load i8**, i8*** %_15
	%_17 = getelementptr i8*, i8** %_16, i32 0
	%_18 = load i8*, i8** %_17
	%_19 = bitcast i8* %_18 to i8* (i8*)*
	%_20 = call i8* %_19(i8* %_14)

	store i8* %_20, i8** %a
	%_21 = load i8*, i8** %a
	%_22 = bitcast i8* %_21 to i8***
	%_23 = load i8**, i8*** %_22
	%_24 = getelementptr i8*, i8** %_23, i32 0
	%_25 = load i8*, i8** %_24
	%_26 = bitcast i8* %_25 to i8* (i8*)*
	%_27 = call i8* %_26(i8* %_21)

	store i8* %_27, i8** %a

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

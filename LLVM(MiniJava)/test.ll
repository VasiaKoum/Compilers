@.Classes_vtable = global [0 x i8*] []

@.Base_vtable = global [2 x i8*] [
	i8* bitcast (i32 (i8*, i32)* @Base.set to i8*),
	i8* bitcast (i32 (i8*)* @Base.get to i8*)
]

@.Derived_vtable = global [3 x i8*] [
	i8* bitcast (i32 (i8*, i32)* @Derived.ret to i8*),
	i8* bitcast (i32 (i8*, i32)* @Derived.set to i8*),
	i8* bitcast (i32 (i8*)* @Base.get to i8*)
]

@.Derived2_vtable = global [5 x i8*] [
	i8* bitcast (i32 (i8*, i32)* @Derived.ret to i8*),
	i8* bitcast (i32 (i8*, i32)* @Derived.set to i8*),
	i8* bitcast (i32 (i8*)* @Base.get to i8*),
	i8* bitcast (i8* (i8*, i32, i32)* @Derived2.vasvas to i8*),
	i8* bitcast (i32 (i8*, i32)* @Derived2.vasia to i8*)
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
	%d = alloca i8*




	call void (32) @print_int(i32 %null)



	call void (32) @print_int(i32 %null)

	ret i32 0
}

define i32 @Base.set(i8* %this, i32 %.x) {
	%x = alloca i32
	store i32 %.x, i32* %x


	%_0 = load i32, i32* %data
	ret i32 %_1
}

define i32 @Base.get(i8* %this) {

	%_0 = load i32, i32* %data
	ret i32 %_1
}

define i32 @Derived.set(i8* %this, i32 %.x) {
	%x = alloca i32
	store i32 %.x, i32* %x
	%b = alloca i8*

	%_0 = load i32, i32* %x
	%_1 = mul i32 %_0, 2

	%_2 = load i32, i32* %x
	ret i32 %_3
}

define i32 @Derived.ret(i8* %this, i32 %.x) {
	%x = alloca i32
	store i32 %.x, i32* %x

	%_0 = load i32, i32* %x
	%_1 = mul i32 %_0, 2

	%_2 = load i32, i32* %data
	ret i32 %_3
}

define i32 @Derived2.vasia(i8* %this, i32 %.x) {
	%x = alloca i32
	store i32 %.x, i32* %x

	%_0 = load i32, i32* %x
	%_1 = mul i32 %_0, 2

	%_2 = load i32, i32* %b
	%_3 = load i32, i32* %b
	%_4 = mul i32 %_2, %_3

	%_5 = load i32, i32* %d
	%_6 = mul i32 %_5, 2

	%_7 = load i32, i32* %data
	ret i32 %_8
}

define i8* @Derived2.vasvas(i8* %this, i32 %.x, i32 %.r) {
	%x = alloca i32
	store i32 %.x, i32* %x
	%r = alloca i32
	store i32 %.r, i32* %r
	%b = alloca i8*

	%_0 = load i32, i32* %x
	%_1 = mul i32 %_0, 2

	%_2 = load i8*, i8** %b
	ret i8* %_3
}

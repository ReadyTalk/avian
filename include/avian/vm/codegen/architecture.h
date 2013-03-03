/* Copyright (c) 2008-2012, Avian Contributors

   Permission to use, copy, modify, and/or distribute this software
   for any purpose with or without fee is hereby granted, provided
   that the above copyright notice and this permission notice appear
   in all copies.

   There is NO WARRANTY for this software.  See license.txt for
   details. */

#ifndef AVIAN_CODEGEN_ARCHITECTURE_H
#define AVIAN_CODEGEN_ARCHITECTURE_H

namespace vm {
class Allocator;
class Zone;
}

namespace avian {
namespace codegen {

class Assembler;

class RegisterFile;

class OperandMask {
public:
  uint8_t typeMask;
  uint64_t registerMask;

  OperandMask(uint8_t typeMask, uint64_t registerMask):
    typeMask(typeMask),
    registerMask(registerMask)
  { }

  OperandMask():
    typeMask(~0),
    registerMask(~static_cast<uint64_t>(0))
  { }
};

class Architecture {
public:
virtual unsigned floatRegisterSize() = 0;

virtual const RegisterFile* registerFile() = 0;

virtual int scratch() = 0;
virtual int stack() = 0;
virtual int thread() = 0;
virtual int returnLow() = 0;
virtual int returnHigh() = 0;
virtual int virtualCallTarget() = 0;
virtual int virtualCallIndex() = 0;

virtual bool bigEndian() = 0;

virtual uintptr_t maximumImmediateJump() = 0;

virtual bool alwaysCondensed(lir::BinaryOperation op) = 0;
virtual bool alwaysCondensed(lir::TernaryOperation op) = 0;

virtual bool reserved(int register_) = 0;

virtual unsigned frameFootprint(unsigned footprint) = 0;
virtual unsigned argumentFootprint(unsigned footprint) = 0;
virtual bool argumentAlignment() = 0;
virtual bool argumentRegisterAlignment() = 0;
virtual unsigned argumentRegisterCount() = 0;
virtual int argumentRegister(unsigned index) = 0;

virtual bool hasLinkRegister() = 0;

virtual unsigned stackAlignmentInWords() = 0;

virtual bool matchCall(void* returnAddress, void* target) = 0;

virtual void updateCall(lir::UnaryOperation op, void* returnAddress,
                        void* newTarget) = 0;

virtual void setConstant(void* dst, uint64_t constant) = 0;

virtual unsigned alignFrameSize(unsigned sizeInWords) = 0;

virtual void nextFrame(void* start, unsigned size, unsigned footprint,
                       void* link, bool mostRecent,
                       unsigned targetParameterFootprint, void** ip,
                       void** stack) = 0;
virtual void* frameIp(void* stack) = 0;
virtual unsigned frameHeaderSize() = 0;
virtual unsigned frameReturnAddressSize() = 0;
virtual unsigned frameFooterSize() = 0;
virtual int returnAddressOffset() = 0;
virtual int framePointerOffset() = 0;

virtual void plan
(lir::UnaryOperation op,
 unsigned aSize, OperandMask& aMask,
 bool* thunk) = 0;

virtual void planSource
(lir::BinaryOperation op,
 unsigned aSize, OperandMask& aMask,
 unsigned bSize, bool* thunk) = 0;
 
virtual void planDestination
(lir::BinaryOperation op,
 unsigned aSize, const OperandMask& aMask,
 unsigned bSize, OperandMask& bMask) = 0;

virtual void planMove
(unsigned size, OperandMask& src,
 OperandMask& tmp,
 const OperandMask& dst) = 0; 

virtual void planSource
(lir::TernaryOperation op,
 unsigned aSize, OperandMask& aMask,
 unsigned bSize, OperandMask& bMask,
 unsigned cSize, bool* thunk) = 0; 

virtual void planDestination
(lir::TernaryOperation op,
 unsigned aSize, const OperandMask& aMask,
 unsigned bSize, const OperandMask& bMask,
 unsigned cSize, OperandMask& cMask) = 0;

virtual Assembler* makeAssembler(vm::Allocator*, vm::Zone*) = 0;

virtual void acquire() = 0;
virtual void release() = 0;
};

} // namespace codegen
} // namespace avian

#endif // AVIAN_CODEGEN_ARCHITECTURE_H
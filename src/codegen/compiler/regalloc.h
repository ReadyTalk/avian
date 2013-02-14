/* Copyright (c) 2008-2012, Avian Contributors

   Permission to use, copy, modify, and/or distribute this software
   for any purpose with or without fee is hereby granted, provided
   that the above copyright notice and this permission notice appear
   in all copies.

   There is NO WARRANTY for this software.  See license.txt for
   details. */

#ifndef AVIAN_CODEGEN_COMPILER_REGALLOC_H
#define AVIAN_CODEGEN_COMPILER_REGALLOC_H

#include "common.h"

#include "codegen/lir.h"
#include "codegen/registers.h"

class Aborter;

namespace avian {
namespace codegen {
namespace compiler {

class Context;
class Value;
class SiteMask;
class Resource;
class Read;


class RegisterAllocator {
public:
  Aborter* a;
  const RegisterFile* registerFile;

  RegisterAllocator(Aborter* a, const RegisterFile* registerFile);

};

class Target {
 public:
  static const unsigned MinimumRegisterCost = 0;
  static const unsigned MinimumFrameCost = 1;
  static const unsigned StealPenalty = 2;
  static const unsigned StealUniquePenalty = 4;
  static const unsigned IndirectMovePenalty = 4;
  static const unsigned LowRegisterPenalty = 10;
  static const unsigned Impossible = 20;

  Target(): cost(Impossible) { }

  Target(int index, lir::OperandType type, unsigned cost):
    index(index), type(type), cost(cost)
  { }

  int16_t index;
  lir::OperandType type;
  uint8_t cost;
};

class CostCalculator {
 public:
  virtual unsigned cost(Context* c, SiteMask mask) = 0;
};

unsigned
resourceCost(Context* c, Value* v, Resource* r, SiteMask mask,
             CostCalculator* costCalculator);


bool
pickRegisterTarget(Context* c, int i, Value* v, uint32_t mask, int* target,
                   unsigned* cost, CostCalculator* costCalculator = 0);

int
pickRegisterTarget(Context* c, Value* v, uint32_t mask, unsigned* cost,
                   CostCalculator* costCalculator = 0);

Target
pickRegisterTarget(Context* c, Value* v, uint32_t mask,
                   CostCalculator* costCalculator = 0);

unsigned
frameCost(Context* c, Value* v, int frameIndex, CostCalculator* costCalculator);

Target
pickFrameTarget(Context* c, Value* v, CostCalculator* costCalculator);

Target
pickAnyFrameTarget(Context* c, Value* v, CostCalculator* costCalculator);

Target
pickTarget(Context* c, Value* value, const SiteMask& mask,
           unsigned registerPenalty, Target best,
           CostCalculator* costCalculator);

Target
pickTarget(Context* c, Read* read, bool intersectRead,
           unsigned registerReserveCount, CostCalculator* costCalculator);

} // namespace regalloc
} // namespace codegen
} // namespace avian

#endif // AVIAN_CODEGEN_COMPILER_REGALLOC_H
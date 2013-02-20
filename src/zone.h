/* Copyright (c) 2008-2011, Avian Contributors

   Permission to use, copy, modify, and/or distribute this software
   for any purpose with or without fee is hereby granted, provided
   that the above copyright notice and this permission notice appear
   in all copies.

   There is NO WARRANTY for this software.  See license.txt for
   details. */

#ifndef ZONE_H
#define ZONE_H

#include "system.h"
#include "allocator.h"

#include <avian/util/math.h>

namespace vm {

class Zone: public Allocator {
 public:
  class Segment {
   public:
    Segment(Segment* next, unsigned size):
      next(next), size(size), position(0)
    { }

    Segment* next;
    uintptr_t size;
    uintptr_t position;
    uint8_t data[0];
  };

  Zone(System* s, Allocator* allocator, unsigned minimumFootprint):
    s(s),
    allocator(allocator),
    segment(0),
    minimumFootprint(minimumFootprint < sizeof(Segment) ? 0 :
                     minimumFootprint - sizeof(Segment))
  { }

  ~Zone() {
    dispose();
  }

  void dispose() {
    for (Segment* seg = segment, *next; seg; seg = next) {
      next = seg->next;
      allocator->free(seg, sizeof(Segment) + seg->size);
    }

    segment = 0;
  }

  static unsigned padToPage(unsigned size) {
    return (size + (LikelyPageSizeInBytes - 1))
      & ~(LikelyPageSizeInBytes - 1);
  }

  bool tryEnsure(unsigned space) {
    if (segment == 0 or segment->position + space > segment->size) {
      unsigned size = padToPage
        (avian::util::max
         (space, avian::util::max
          (minimumFootprint, segment == 0 ? 0 : segment->size * 2))
         + sizeof(Segment));

      void* p = allocator->tryAllocate(size);
      if (p == 0) {
        size = padToPage(space + sizeof(Segment));
        p = allocator->tryAllocate(size);
        if (p == 0) {
          return false;
        }
      }

      segment = new (p) Segment(segment, size - sizeof(Segment));
    }
    return true;
  }

  void ensure(unsigned space) {
    if (segment == 0 or segment->position + space > segment->size) {
      unsigned size = padToPage(space + sizeof(Segment));

      segment = new (allocator->allocate(size))
        Segment(segment, size - sizeof(Segment));
    }
  }

  virtual void* tryAllocate(unsigned size) {
    size = pad(size);
    if (tryEnsure(size)) {
      void* r = segment->data + segment->position;
      segment->position += size;
      return r;
    } else {
      return 0;
    }
  }

  virtual void* allocate(unsigned size) {
    size = pad(size);
    void* p = tryAllocate(size);
    if (p) {
      return p;
    } else {
      ensure(size);
      void* r = segment->data + segment->position;
      segment->position += size;
      return r;
    }
  }

  void* peek(unsigned size) {
    size = pad(size);
    Segment* s = segment;
    while (s->position < size) {
      size -= s->position;
      s = s->next;
    }
    return s->data + (s->position - size);
  }

  void pop(unsigned size) {
    size = pad(size);
    Segment* s = segment;
    while (s->position < size) {
      size -= s->position;
      Segment* next = s->next;
      allocator->free(s, sizeof(Segment) + s->size);
      s = next;
    }
    s->position -= size;
    segment = s;
  }

  virtual void free(const void*, unsigned) {
    // not supported
    abort(s);
  }
  
  System* s;
  Allocator* allocator;
  void* context;
  Segment* segment;
  unsigned minimumFootprint;
};

} // namespace vm

#endif//ZONE_H

import java.util.EmptyStackException;

public class LongStack {
   public static void main(String[] args) {
      LongStack stack = new LongStack();

      // test push and pop methods
      stack.push(1);
      stack.push(2);
      stack.push(3);
      assert(stack.pop() == 3);
      assert(stack.pop() == 2);
      assert(stack.pop() == 1);

      // test arithmetic operations
      stack.push(2);
      stack.push(3);
      stack.op("+");
      assert(stack.pop() == 5);

      stack.push(10);
      stack.push(2);
      stack.op("-");
      assert(stack.pop() == 8);

      stack.push(4);
      stack.push(5);
      stack.op("*");
      assert(stack.pop() == 20);

      stack.push(10);
      stack.push(5);
      stack.op("/");
      assert(stack.pop() == 2);

      // test expression evaluation
      assert(LongStack.interpret("5 2 + 8 * 4 /") == 9);
      assert(LongStack.interpret("3 4 2 * 1 5 - 2 3 ^ ^ / +") == 3);
   }

   public class StackEmptyException extends RuntimeException {
      public StackEmptyException(String message) {
         super(message);
      }
   }

   private Node top;
   private int size;

   private static class Node {
      long value;
      Node next;

      Node(long value, Node next) {
         this.value = value;
         this.next = next;
      }
   }

   public LongStack() {
      top = null;
      size = 0;
   }

   @Override
   public Object clone() throws CloneNotSupportedException {
      LongStack cloned = new LongStack();
      Node node = top;
      while (node != null) {
         cloned.push(node.value);
         node = node.next;
      }
      LongStack reversed = new LongStack();
      node = cloned.top;
      while (node != null) {
         reversed.push(node.value);
         node = node.next;
      }
      return reversed;
   }

   public boolean stEmpty() {
      return top == null;
   }

   public void push(long a) {
      top = new Node(a, top);
      size++;
   }

   public long pop() {
      if (top == null) {
         throw new StackEmptyException("Stack is empty");
      }
      long value = top.value;
      top = top.next;
      size--;
      return value;
   }

   public void op(String s) {
      if (top == null || top.next == null) {
         throw new IllegalStateException("Stack underflow");
      }
      long b = pop();
      long a = pop();
      long result;
      switch (s) {
         case "+":
            result = a + b;
            break;
         case "-":
            result = a - b;
            break;
         case "*":
            result = a * b;
            break;
         case "/":
            if (b == 0) {
               throw new IllegalArgumentException("Division by zero: " + s);
            }
            result = a / b;
            break;
         default:
            throw new IllegalArgumentException("Invalid operator: " + s);
      }
      push(result);
   }

   public long tos() {
      if (top == null) {
         throw new StackEmptyException("Stack is empty");
      }
      return top.value;
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      }
      if (!(o instanceof LongStack)) {
         return false;
      }
      LongStack other = (LongStack) o;
      if (size != other.size) {
         return false;
      }
      Node node1 = top;
      Node node2 = other.top;
      while (node1 != null && node2 != null) {
         if (node1.value != node2.value) {
            return false;
         }
         node1 = node1.next;
         node2 = node2.next;
      }
      return true;
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder();
      Node node = top;
      while (node != null) {
         sb.insert(0, node.value + " ");
         node = node.next;
      }
      sb.append("-");
      return sb.toString().trim();
   }


   public static long interpret(String pol) {
      String[] tokens = pol.trim().split("\\s+");
      LongStack stack = new LongStack();
      for (String token : tokens) {
         try {
            long value = Long.parseLong(token);
            stack.push(value);
         } catch (NumberFormatException e) {
            try {
               stack.op(token);
            } catch (EmptyStackException ex) {
               throw new IllegalArgumentException("Invalid expression: " + pol + ". Not enough operands for operator: " + token);
            } catch (IllegalStateException ex) {
               throw new IllegalArgumentException("Invalid expression: " + pol + ". Not enough operands for operator: " + token);
            } catch (IllegalArgumentException ex) {
               throw new IllegalArgumentException("Invalid expression: " + pol + ". " + ex.getMessage());
            } catch (Exception ex) {
               throw new RuntimeException("Invalid expression: " + pol, ex);
            }
         }
      }
      try {
         long result = stack.pop();
         if (!stack.stEmpty()) {
            throw new IllegalArgumentException("Invalid expression: " + pol + ". Too many operands.");
         }
         return result;
      } catch (EmptyStackException ex) {
         throw new IllegalArgumentException("Invalid expression: " + pol + ". Not enough operands.");
      }
   }

}

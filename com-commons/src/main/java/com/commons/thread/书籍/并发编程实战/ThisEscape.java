package com.commons.thread.书籍.并发编程实战;

/**
 * ThisEscape
 * <p/>
 * Implicitly allowing the this reference to escape
 *
 * @author Brian Goetz and Tim Peierls
 */
public class ThisEscape {
	public static void main(String[] args) {
		ThisEscape te = new ThisEscape(new EventSource() {
			
			@Override
			public void registerListener(EventListener e) {
				System.out.println("2");
				e.onEvent(new Event(){});
				System.out.println("6");
			}
		});
		System.out.println(te);
	}
    public ThisEscape(EventSource source) {
    	System.out.println(source);
    	System.out.println("1");
        source.registerListener(new EventListener() {
            public void onEvent(Event e) {
            	System.out.println("3");
                doSomething(e);
                System.out.println("5");
            }
        });
        System.out.println("7");
    }

    void doSomething(Event e) {
    	System.out.println("4");
    }


    interface EventSource {
        void registerListener(EventListener e);
    }

    interface EventListener {
        void onEvent(Event e);
    }

    interface Event {
    }
}


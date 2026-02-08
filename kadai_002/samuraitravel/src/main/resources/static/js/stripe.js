const stripe = Stripe('pk_test_51SiUgRPUAoWNu6wEI5uUFTJtMB3bxJSQZf5Rlyr3MZjVrZnhP2vMJDbofjhyhjwcleHIIReFumr4tSJIOq2LU4hE005ThmOUjo');
const paymentButton = document.querySelector('#paymentButton');

paymentButton.addEventListener('click', () => {
 stripe.redirectToCheckout({
   sessionId: sessionId
 })
});
!function(e){var t={};function o(n){if(t[n])return t[n].exports;var r=t[n]={i:n,l:!1,exports:{}};return e[n].call(r.exports,r,r.exports,o),r.l=!0,r.exports}o.m=e,o.c=t,o.d=function(e,t,n){o.o(e,t)||Object.defineProperty(e,t,{enumerable:!0,get:n})},o.r=function(e){"undefined"!=typeof Symbol&&Symbol.toStringTag&&Object.defineProperty(e,Symbol.toStringTag,{value:"Module"}),Object.defineProperty(e,"__esModule",{value:!0})},o.t=function(e,t){if(1&t&&(e=o(e)),8&t)return e;if(4&t&&"object"==typeof e&&e&&e.__esModule)return e;var n=Object.create(null);if(o.r(n),Object.defineProperty(n,"default",{enumerable:!0,value:e}),2&t&&"string"!=typeof e)for(var r in e)o.d(n,r,function(t){return e[t]}.bind(null,r));return n},o.n=function(e){var t=e&&e.__esModule?function(){return e.default}:function(){return e};return o.d(t,"a",t),t},o.o=function(e,t){return Object.prototype.hasOwnProperty.call(e,t)},o.p="/dist/",o(o.s=3)}({3:function(e,t){document.addEventListener("DOMContentLoaded",function(e){const t=document.querySelector(".country-selector");if(!t)return;const o=(function(e){let t=0,o=0;do{t+=e.offsetTop||0,o+=e.offsetLeft||0,e=e.offsetParent}while(e);return{top:t,left:o}})(t).top,n=document.body,r=document.documentElement,u=Math.max(n.scrollHeight,n.offsetHeight,r.clientHeight,r.scrollHeight,r.offsetHeight),i=window.getComputedStyle(t).getPropertyValue("position");window.getComputedStyle(t).getPropertyValue("bottom"),window.getComputedStyle(t).getPropertyValue("top");return("fixed"!==i&&o>u/2||"fixed"===i&&o>100)&&(t.className+=" weglot-invert"),!1})}});

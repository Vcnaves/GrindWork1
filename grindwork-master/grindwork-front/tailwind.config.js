/** @type {import('tailwindcss').Config} */
export default {
  content: ["./index.html", "./src/**/*.{js,ts,jsx,tsx}"],
  theme: {
    extend: {
      colors: {
        greenLogo: '#002f34',
        orangeLogo: '#ff7828',
      },
    },
  },
  plugins: [],
};

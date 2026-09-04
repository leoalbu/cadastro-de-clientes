import type { Config } from 'tailwindcss'

const config: Config = {
  content: [
    './src/pages/**/*.{js,ts,jsx,tsx,mdx}',
    './src/components/**/*.{js,ts,jsx,tsx,mdx}',
    './src/app/**/*.{js,ts,jsx,tsx,mdx}',
  ],
  theme: {
    extend: {
      colors: {
        fundo: '#F4F6F8',
        superficie: '#FFFFFF',
        'superficie-2': '#EEF1F4',
        borda: '#E2E6EB',
        'borda-forte': '#C9D0D8',
        tinta: '#1B2431',
        'tinta-suave': '#5C6774',
        'tinta-fraca': '#8B95A1',
        acento: { DEFAULT: '#0F766E', hover: '#0B5C55', suave: '#DCEFE9' },
        vencido: { DEFAULT: '#B3261E', bg: '#FBE4E2' },
        proximo: { DEFAULT: '#8A5200', bg: '#FBEECD' },
        ok: { DEFAULT: '#0F766E', bg: '#DCEFE9' },
        inativo: { DEFAULT: '#8B95A1', bg: '#EDEFF2' },
      },
      fontFamily: {
        sans: ['var(--font-inter)', 'Segoe UI', 'sans-serif'],
      },
      backgroundImage: {
        'gradient-radial': 'radial-gradient(var(--tw-gradient-stops))',
        'gradient-conic':
          'conic-gradient(from 180deg at 50% 50%, var(--tw-gradient-stops))',
      },
    },
  },
  plugins: [],
}
export default config

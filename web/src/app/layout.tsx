import type { Metadata } from "next";
import { Inter } from "next/font/google";
import "./globals.css";

const inter = Inter({ subsets: ["latin"], variable: "--font-inter" });

export const metadata: Metadata = {
  title: "Cadastro de Clientes",
  description: "Cadastro de clientes com controle de valores a receber, integrado ao Supabase.",
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="pt-BR" className={inter.variable}>
      <body className="bg-fundo text-tinta antialiased">{children}</body>
    </html>
  );
}

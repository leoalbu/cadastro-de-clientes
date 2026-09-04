/** Validação de CPF/CNPJ e e-mail, e cálculo de situação financeira. Sem dependências. */

export function somenteDigitos(v: string | null | undefined): string {
  return (v || "").replace(/\D/g, "");
}

function cpfValido(cpf: string): boolean {
  if (cpf.length !== 11 || /^(\d)\1{10}$/.test(cpf)) return false;
  const dv = (qtd: number, pesoIni: number) => {
    let soma = 0;
    for (let i = 0; i < qtd; i++) soma += Number(cpf[i]) * (pesoIni - i);
    const r = soma % 11;
    return r < 2 ? 0 : 11 - r;
  };
  return dv(9, 10) === Number(cpf[9]) && dv(10, 11) === Number(cpf[10]);
}

function cnpjValido(cnpj: string): boolean {
  if (cnpj.length !== 14 || /^(\d)\1{13}$/.test(cnpj)) return false;
  const dv = (pesos: number[]) => {
    let soma = 0;
    for (let i = 0; i < pesos.length; i++) soma += Number(cnpj[i]) * pesos[i];
    const r = soma % 11;
    return r < 2 ? 0 : 11 - r;
  };
  const d1 = dv([5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2]);
  const d2 = dv([6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2]);
  return d1 === Number(cnpj[12]) && d2 === Number(cnpj[13]);
}

export function docValido(v: string): boolean {
  const d = somenteDigitos(v);
  if (d.length === 11) return cpfValido(d);
  if (d.length === 14) return cnpjValido(d);
  return false;
}

export function formatarDoc(v: string | null | undefined): string {
  const d = somenteDigitos(v);
  if (d.length === 11) return d.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, "$1.$2.$3-$4");
  if (d.length === 14) return d.replace(/(\d{2})(\d{3})(\d{3})(\d{4})(\d{2})/, "$1.$2.$3/$4-$5");
  return v || "";
}

export function emailValido(e: string): boolean {
  return /^[\w.+-]+@[\w-]+(\.[\w-]+)+$/.test(e.trim());
}

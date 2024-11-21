import { FaChevronLeft } from "react-icons/fa";

const TermosCondicoes = () => {
  const goBack = () => {
    window.history.back();
  };

  return (
    <>
      <div className="relative w-full lg:w-full bg-slate-200 z-0">
        <div className="flex w-full h-10 rounded-md items-center justify-between">
          <button
            className="flex text-slate-700 mr-2 hover:text-gray-400"
            onClick={goBack}
          >
            <FaChevronLeft className="mt-1.5 mr-1" />
            Voltar
          </button>
        </div>
      </div>
      <div className="flex flex-col items-center justify-center min-h-screen bg-gray-100">
        <div className="bg-white p-8 rounded-sm shadow-md max-w-3xl">
          <h1 className="text-2xl font-bold mb-4">
            Termos e Condições - GrindWork
          </h1>
          <p className="mb-4">
            Bem-vindo ao GrindWork! GrindWork é uma plataforma para conectar
            prestadores de serviços e clientes. Ao utilizar nosso site, você
            concorda com os termos e condições abaixo.
          </p>

          <h2 className="text-xl font-semibold mt-4">
            1. Serviço de Divulgação
          </h2>
          <p className="mb-4">
            GrindWork atua como um mediador entre prestadores de serviços e
            clientes. Nosso serviço é gratuito e não cobramos taxas ou
            comissões.
          </p>

          <h2 className="text-xl font-semibold mt-4">2. Responsabilidade</h2>
          <p className="mb-4">
            GrindWork não é responsável por golpes, fraudes ou problemas nos
            anúncios. Usuários devem verificar a veracidade das informações
            antes de qualquer interação ou contratação.
          </p>

          <h2 className="text-xl font-semibold mt-4">3. Bloqueio de Contas</h2>
          <p className="mb-4">
            Anúncios ou contas que forem descobertos como fraudulentos ou
            suspeitos terão seus acessos suspensos e bloqueados permanentemente.
          </p>

          <h2 className="text-xl font-semibold mt-4">
            4. Aceitação dos Termos
          </h2>
          <p className="mb-4">
            Ao utilizar o GrindWork, você concorda com todos os termos acima.
            Estes termos podem ser atualizados a qualquer momento, e
            recomendamos que consulte esta página regularmente.
          </p>
        </div>
      </div>
    </>
  );
};

export default TermosCondicoes;

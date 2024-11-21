import { useEffect, useState } from "react";
import { FaChevronLeft, FaChevronRight } from "react-icons/fa";
import Api from "../api/Api";
import Swal from "sweetalert2";

interface LocalidadeComboBoxProps {
  onSelectLocalidade: (estado: string, cidade: string) => void;
}

const LocalidadeComboBox = ({
  onSelectLocalidade,
}: LocalidadeComboBoxProps) => {
  const [estadoSelecionado, setEstadoSelecionado] = useState<string | null>(
    null
  );
  const [, setCidadeSelecionada] = useState<string | null>(
    null
  );
  const [estadosECidades, setEstadosECidades] = useState<
    { estado: string; cidades: string[] }[]
  >([]);
  const [filtroCidade, setFiltroCidade] = useState<string>("");

  const handleEstadoClick = (estado: string) => {
    setEstadoSelecionado(estado);
    setCidadeSelecionada(null);
    setFiltroCidade("");
  };

  const getLocalidades = async () => {
    try {
      const response = await Api.get(`/localidades/estados-cidades`);
      setEstadosECidades(response.data || []);
    } catch (error: any) {
      console.error("ops! ocorreu um erro: " + error);
      Swal.fire({
        title: "Erro ao buscar localidade!",
        text: `${error.response.data.msg}`,
        icon: "error"
      });
    }
  };

  useEffect(() => {
    getLocalidades();
  }, []);

  const handleCidadeClick = (cidade: string) => {
    setCidadeSelecionada(cidade);
    if (estadoSelecionado) {
      onSelectLocalidade(estadoSelecionado, cidade);
    }
  };

  const cidadesFiltradas = estadoSelecionado
    ? estadosECidades
        .find((item) => item.estado === estadoSelecionado)
        ?.cidades.filter((cidade) =>
          cidade.toLowerCase().includes(filtroCidade.toLowerCase())
        ) || []
    : [];

  return (
    <div className="relative w-flex bg-white shadow-lg rounded-lg border border-gray-200">
      {!estadoSelecionado ? (
        <div>
          <div className="p-4 border-b border-gray-200">
            <h3 className="text-lg font-semibold text-gray-700">
              Escolher uma região
            </h3>
          </div>
          <div className="divide-y divide-gray-200 ">
            {estadosECidades
              .sort((a, b) => a.estado.localeCompare(b.estado))
              .map((item, index) => (
                <div
                  key={`${item.estado}-${index}`}
                  className="flex w-full justify-between h-14 px-4 py-2 cursor-pointer hover:bg-gray-100"
                  onClick={() => handleEstadoClick(item.estado)}
                >
                  <div className="text-lg">{item.estado}</div>
                  <FaChevronRight className=" mt-1 text-slate-500 size-6 " />
                </div>
              ))}
          </div>
        </div>
      ) : (
        <div>
          <div className="p-4 border-b border-gray-200 flex items-center">
            <button
              className=" flex text-blue-500 mr-2 hover:text-blue-700"
              onClick={() => setEstadoSelecionado(null)}
            >
              <FaChevronLeft className="mt-1.5 mr-1"/>
               Voltar
            </button>
            <h3 className="text-lg font-semibold text-gray-700">
              {estadoSelecionado}
            </h3>
          </div>
          <div className="p-4 border-b border-gray-200">
            <input
              type="text"
              placeholder="Filtrar cidades..."
              value={filtroCidade}
              onChange={(e) => setFiltroCidade(e.target.value)}
              className="w-full border border-gray-300 rounded-md p-2"
            />
          </div>
          <div
            className="divide-y divide-gray-200"
            style={{ maxHeight: "200px", overflowY: "auto" }}
          >
            {cidadesFiltradas
              .sort((a, b) => a.localeCompare(b))
              .map((cidade, index) => (
                <div
                  key={`${estadoSelecionado}-${cidade}-${index}`}
                  className="px-4 py-2 cursor-pointer hover:bg-gray-100"
                  onClick={() => handleCidadeClick(cidade)}
                >
                  {cidade}
                </div>
              ))}
          </div>
        </div>
      )}
    </div>
  );
};

export default LocalidadeComboBox;

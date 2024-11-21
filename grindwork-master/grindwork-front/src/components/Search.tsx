import { useState } from "react";
import Lens from "../assets/search-icon.png";
import Pin from "../assets/location-icon.png";
import { FaSearch, FaTimes } from "react-icons/fa";
import LocalidadeComboBox from "./LocalidadeComboBox"; 

interface Props {
  setSearch: (value: string) => void;
  setLocalidade: (value: string) => void; 
  onSearch: () => void; 
}

const Search = ({ setSearch, setLocalidade, onSearch }: Props) => {
  const [showLocalidadeDropdown, setShowLocalidadeDropdown] = useState(false); 
  const [localidadeSelecionada, setLocalidadeSelecionada] = useState<string | null>(null);

  // Função para alternar o dropdown de localidade
  const handleLocalidadeClick = () => {
    setShowLocalidadeDropdown((prev) => !prev);
  };

  // Função para limpar a localidade selecionada
  const handleClearLocalidade = () => {
    setLocalidadeSelecionada(null);
    setLocalidade(""); 
  };

  return (
    <div className="flex w-full flex-col lg:flex-row p-6 justify-center lg:justify-center items-center space-y-4 lg:space-y-0 lg:space-x-4 bg-slate-100">
      {/* Campo de busca */}
      <div className="flex w-full lg:w-96 bg-slate-200 rounded-md">
        <div className="flex w-full ml-2">
          <img src={Lens} alt="grindwork-logo" className="w-6 h-6 mt-3" />
          <input
            type="text"
            placeholder="Qual serviço você precisa?"
            className="w-full border h-12 p-2 bg-slate-200 rounded-md"
            onChange={(e) => setSearch(e.target.value)} 
          />
        </div>
      </div>

      {/* Campo de localidade */}
      <div className="relative w-full lg:w-64">
        <div className="flex w-full bg-slate-200 rounded-md items-center">
          <div className="flex w-full ml-2 cursor-pointer" onClick={handleLocalidadeClick}>
            <img src={Pin} alt="grindwork-logo" className="w-6 h-6 mt-3" />
            <input
              type="text"
              placeholder="Localidade"
              className="w-full border h-12 p-2 bg-slate-200 rounded-md cursor-pointer"
              value={localidadeSelecionada || ""}
              readOnly
            />
          </div>

          {/* Ícone X para limpar a localidade */}
          {localidadeSelecionada && (
            <FaTimes
              className="w-5 h-5 text-gray-500 mr-2 cursor-pointer"
              onClick={handleClearLocalidade} 
            />
          )}
        </div>

        {/* Dropdown de localidade */}
        {showLocalidadeDropdown && (
          <div className="absolute z-50 top-14 left-0 w-full bg-white shadow-lg rounded-md">
            <LocalidadeComboBox
              onSelectLocalidade={(estado: any, cidade: any) => {
                setLocalidadeSelecionada(`${cidade}, ${estado}`);
                setLocalidade(`${cidade}, ${estado}`); 
                setShowLocalidadeDropdown(false); 
              }}
            />
          </div>
        )}
      </div>

      {/* Botão de pesquisa */}
      <div
        className="flex w-full lg:w-28 h-12 p-3 cursor-pointer rounded-md border border-greenLogo bg-greenLogo text-white hover:bg-white hover:text-greenLogo active:bg-orangeLogo focus:outline-none focus:ring focus:ring-greenLogo justify-center"
        onClick={onSearch} 
      >
        <h1 className="font-bold text-lg ml-1">Pesquisar</h1>
        <FaSearch className="w-5 h-5 mt-1 m-1" />
      </div>
    </div>
  );
};

export default Search;

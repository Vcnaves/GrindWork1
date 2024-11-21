import { BiSolidDirections } from "react-icons/bi";

interface Props {
  placeholder?: string;
  options: {
    value: string; // Normalmente o código do país, como "cca2"
    label: string; // O nome oficial do país em português
  }[];
  value?: { value: string; label: string };
  onChange: (event: React.ChangeEvent<HTMLSelectElement>) => void; // Função de callback para capturar a seleção
msgopcional?: string;
name?:string;
}

const SelectField = ({ placeholder, options,value, onChange, msgopcional, name }: Props) => {
  return (
    <>
      <div className="flex w-full mt-2 bg-slate-200 rounded-md ">
        <div className="flex w-full ml-2">
          <BiSolidDirections className="h-full mr-1 text-gray-800" />
          <select
            id="country"
            name={name}
            autoComplete="country-name"
            className="w-full border h-12 p-2 bg-slate-200 rounded-md text-gray-800"
            onChange={onChange}
            value={value?.value}
          >
            <option value="">{placeholder}</option>
            {options.length > 0 ? (
              options.map((option) => (
                <option key={option.value} value={option.value}>
                  {option.label} 
                </option>
              ))
            ) : (
              <option>{msgopcional || "Carregando..."}</option>
            )}
          </select>
        </div>
      </div>
    </>
  );
};

export default SelectField;

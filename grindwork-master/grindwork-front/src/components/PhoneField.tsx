import { useMask } from "@react-input/mask";
import { FaWhatsapp } from "react-icons/fa";

interface Props{
  onChange: (event: React.ChangeEvent<HTMLInputElement>) => void;
  value?: string;
  name?:string;
}

const PhoneField = ({onChange, value, name}:Props) => {
  const inputRef = useMask({
    mask: "(__) _ ____-____",
    replacement: { _: /\d/ },
  });
  return (
    <>
      <div className="flex w-full mt-2 bg-slate-200 rounded-md">
        <div className="flex w-full ml-2">
          <FaWhatsapp className=" h-full mr-1 text-gray-800" />
          <input
          name={name}
            type="tel"
            ref={inputRef}
            placeholder="Digite seu celular"
            onChange={onChange}
            value={value}
            className="block w-full border h-12 p-2 bg-slate-200 rounded-md"
          />
        </div>
      </div>
    </>
  );
};

export default PhoneField;
